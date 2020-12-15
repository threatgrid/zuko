(ns ^{:doc "Converts external data into a graph format (triples)."
      :author "Paula Gearon"}
    zuko.entity.writer
  (:require [zuko.entity.general :as general :refer [tg-ns KeyValue EntityMap GraphType]]
            [zuko.entity.reader :as reader]
            [zuko.node :as node]
            [schema.core :as s :refer [=>]]
            [clojure.string :as string]
            #?(:clj [clojure.java.io :as io])
            #?(:clj [cheshire.core :as j]))
  #?(:clj (:import [java.util Map List])))

#?(:clj  (def parse-json-string #(j/parse-string % true))
   :cljs (def parse-json-string #(js->clj (.parse js/JSON %) :keywordize-keys true)))

#?(:clj
   (defn json-generate-string
     ([data] (j/generate-string data))
     ([data indent]
      (j/generate-string
       data
       (assoc j/default-pretty-print-options
              :indentation (apply str (repeat indent \space))))))

   :cljs
   (defn json-generate-string
     ([data] (.stringify js/JSON (clj->js data)))
     ([data indent] (.stringify js/JSON (clj->js data) nil indent))))

(def ^:dynamic *current-graph* nil)

(def ^:dynamic *id-map* nil)

(def ^:dynamic *triples* nil)

(def Triple [(s/one s/Any "Entity")
             (s/one s/Keyword "attribute")
             (s/one s/Any "value")])

(declare value-triples map->triples)

(defn list-triples
  "Creates the triples for a list.Returns a node and list of nodes representing contents of the list."
  [[v & vs :as vlist]]
  (if (seq vlist)
    (let [node-ref (node/new-node *current-graph*)
          value-ref (value-triples v)
          [next-ref value-nodes] (list-triples vs)]
      (vswap! *triples* conj [node-ref (node/data-attribute *current-graph* value-ref) value-ref])
      (when next-ref
        (vswap! *triples* conj [node-ref :tg/rest next-ref]))
      [node-ref (cons value-ref value-nodes)])))

(s/defn value-triples-list
  [vlist :- [s/Any]]
  (if (seq vlist)
    (let [[node value-nodes] (list-triples vlist)]
      (doseq [vn value-nodes]
        (vswap! *triples* conj [node (node/container-attribute *current-graph* vn) vn]))
      node)
    :tg/empty-list))

(defn value-triples
  "Converts a value into a list of triples.
   Return the entity ID of the data."
  [v]
  (cond
    (sequential? v) (value-triples-list v)
    (set? v) (value-triples-list (seq v))
    (map? v) (map->triples v)
    (nil? v) :tg/nil
    :default v))

(s/defn property-vals
  "Takes a property-value pair associated with an entity,
   and builds triples around it"
  [entity-ref :- s/Any
   [property value] :- KeyValue]
  (if (set? value)
    (doseq [v value]
      (let [vr (value-triples v)]
        (vswap! *triples* conj [entity-ref property vr])))
    (let [v (value-triples value)]
      (vswap! *triples* conj [entity-ref property v]))))

(defn new-node
  [id]
  (let [next-id (node/new-node *current-graph*)]
    (vswap! *id-map* assoc (or id next-id) next-id)
    next-id))

(s/defn get-ref
  "Returns the reference for an object, and a flag to indicate if a new reference was generated"
  [{id :db/id ident :db/ident :as data} :- {s/Keyword s/Any}]
  (if-let [r (@*id-map* id)] ;; an ID that is already mapped
    [r false]
    (cond ;; a negative ID is a request for a new saved ID
      (and (number? id) (neg? id)) [(new-node id) false]
      ;; Use the provided ID
      id (if (node/node-type? *current-graph* :db/id id)
           [id false]
           (throw (ex-info ":db/id must be a value node type" {:db/id id})))
      ;; With no ID do an ident lookup
      ident (if-let [r (@*id-map* ident)]
              [r true]
              (let [lookup (node/find-triple *current-graph* ['?n :db/ident ident])]
                (if (seq lookup)
                  (let [read-id (ffirst lookup)]
                    (vswap! *id-map* assoc ident read-id)
                    [read-id true]) ;; return the retrieved ref
                  [(new-node ident) false]))) ;; nothing retrieved so generate a new ref
      ;; generate an ID
      :default [(new-node nil) false])))  ;; generate a new ref


(s/defn map->triples
  "Converts a single map to triples. Returns the entity reference or node id."
  [data :- {s/Keyword s/Any}]
  (let [[entity-ref ident?] (get-ref data)
        data (dissoc data :db/id)
        data (if ident? (dissoc data :db/ident) data)]
    (doseq [d data]
      (property-vals entity-ref d))
    entity-ref))


(defn name-for
  "Convert an id (probably a number) to a keyword for identification"
  [id]
  (if (keyword? id)
    id
    (node/node-label *current-graph* id)))


(s/defn ident-map->triples
  "Converts a single map to triples for an ID'ed map"
  ([graph :- GraphType
    j :- EntityMap]
   (ident-map->triples graph j {}))
  ([graph :- GraphType
    j :- EntityMap
    id-map :- {s/Any s/Any}]
   (binding [*current-graph* graph
             *id-map* (volatile! id-map)
             *triples* (volatile! [])]
     (ident-map->triples j)
     [@*triples* @*id-map*]))
  ([j :- EntityMap]
   (let [node-ref (map->triples j)]
     (if (:db/ident j)
       (vswap! *triples* conj [node-ref :tg/entity true])
       (vswap! *triples* into [[node-ref :db/ident (name-for node-ref)] [node-ref :tg/entity true]]))
     @*id-map*)))


(s/defn entities->triples :- [Triple]
  "Converts objects into a sequence of triples."
  ([graph :- GraphType
    entities :- [EntityMap]]
   (entities->triples graph entities {}))
  ([graph :- GraphType
    entities :- [EntityMap]
    id-map :- {s/Any s/Any}]
   (binding [*current-graph* graph
             *id-map* (volatile! id-map)
             *triples* (volatile! [])]
     (doseq [e entities]
       (ident-map->triples e))
     @*triples*)))


#?(:clj
    (s/defn stream->triples :- [Triple]
      "Converts a stream to triples"
      [graph :- GraphType
       io]
      (with-open [r (io/reader io)]
        (let [data (j/parse-stream r true)]
          (entities->triples graph data))))

   :cljs
    (s/defn stream->triples :- [Triple]
      [graph io]
      (throw (ex-info "Unsupported IO" {:io io}))))

(s/defn string->triples :- [Triple]
  "Converts a string to triples"
  [graph :- GraphType
   s :- s/Str]
  (entities->triples graph (parse-json-string s)))


;; updating the store

(s/defn existing-triples
  [graph :- GraphType
   node-ref
   [k v]]
  (if-let [subpv (reader/check-structure graph k v)]
    (cons [node-ref k v] (mapcat (partial existing-triples graph v) subpv))
    [[node-ref k v]]))

(s/defn entity-update->triples :- [(s/one [Triple] "assertions") (s/one [Triple] "retractions")]
  "Takes a single structure and converts it into triples to be added and triples to be retracted to create a change"
  [graph :- GraphType
   node-ref  ;; a reference for the structure to be updated
   entity]   ;; the structure to update the structure in the database to
  (binding [*current-graph* graph
            *id-map* (volatile! {})]
    (let [pvs (reader/property-values graph node-ref)
          old-node (reader/pairs->struct graph pvs)
          to-remove (remove (fn [[k v]] (if-let [newv (get entity k)] (= v newv))) old-node)
          pvs-to-remove (filter (comp (set (map first to-remove)) first) pvs)
          triples-to-remove (mapcat (partial existing-triples graph node-ref) pvs-to-remove)

          to-add (remove (fn [[k v]] (when-let [new-val (get old-node k)] (= new-val v))) entity)
          triples-to-add (binding [*triples* (volatile! [])]
                           (doseq [pvs to-add] (property-vals node-ref pvs))
                           @*triples*)]
      [triples-to-add triples-to-remove])))
