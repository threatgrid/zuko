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

(def Triple [(s/one s/Any "Entity")
             (s/one s/Keyword "attribute")
             (s/one s/Any "value")])

(def EntityTriplesPair [(s/one s/Any "node ID")
                        (s/one [Triple] "current list of triples")])

(s/defn containership-triples
  "Finds the list of entity nodes referred to in a list and builds
   triples describing a flat 'contains' property"
  [node :- s/Any
   triples :- [Triple]]
  (let [listmap (->> (group-by first triples)
                     (map (fn [[k vs]]
                            [k (into {} (map #(apply vector (rest %)) vs))]))
                     (into {}))
        node-list (loop [nl [] n node]
                    (if-not n
                      nl
                      (let [{r :tg/rest :as lm} (listmap n)
                            [_ f] (reader/get-tg-first lm)]
                        (recur (conj nl f) r))))]
    (doall  ;; uses a dynamically bound value, so ensure that this is executed
      (map
       (fn [n] [node (node/container-attribute *current-graph* n) n])
       node-list))))

(declare value-triples map->triples)

(s/defn list-triples
  "Creates the triples for a list"
  [[v & vs :as vlist]]
  (if (seq vlist)
    (let [node-ref (node/new-node *current-graph*)
          [value-ref triples] (value-triples v)
          [next-ref next-triples] (list-triples vs)]
      [node-ref (concat [[node-ref (node/data-attribute *current-graph* value-ref) value-ref]]
                  (when next-ref [[node-ref :tg/rest next-ref]])
                  triples
                  next-triples)])))

(s/defn value-triples-list :- EntityTriplesPair
  [vlist :- [s/Any]]
  (let [[node triples :as raw-result] (list-triples vlist)]
    (if triples
      [node (concat triples (containership-triples node triples))]
      raw-result)))

(s/defn value-triples
  "Converts a value into a list of triples.
   Return the entity ID of the data coupled with the sequence of triples."
  [v]
  (cond
    (sequential? v) (value-triples-list v)
    (set? v) (value-triples-list (seq v))
    (map? v) (map->triples v)
    (nil? v) nil
    :default [v nil]))

(s/defn property-vals :- [Triple]
  "Takes a property-value pair associated with an entity,
   and builds triples around it"
  [entity-ref :- s/Any
   [property value] :- KeyValue]
  (if-let [[value-ref value-data] (value-triples value)]
    (cons [entity-ref property value-ref] value-data)))


(s/defn get-ref
  [{id :db/id :as data} :- {s/Keyword s/Any}]
  (or (@*id-map* id)                    ;; an ID that is already mapped
      (if (and (number? id) (neg? id))  ;; a negative ID is a request for a new saved ID
        (let [next-id (node/new-node *current-graph*)]
          (vswap! *id-map* assoc id next-id)
          next-id))
      id                                ;; Use the provided ID
      (node/new-node *current-graph*))) ;; no ID, so create a new one


(s/defn map->triples :- EntityTriplesPair
  "Converts a single map to triples. Returns a pair of the map's ID and the triples for the map."
  [data :- {s/Keyword s/Any}]
  (let [entity-ref (get-ref data)
        data' (dissoc data :db/id)]
    [entity-ref (if (seq data')
                  (doall (mapcat (partial property-vals entity-ref) data')))]))


(s/defn name-for
  "Convert an id (probably a number) to a keyword for identification"
  [id :- s/Any]
  (if (keyword? id)
    id
    (node/node-label *current-graph* id)))


(s/defn ident-map->triples :- [(s/one [Triple] "The triples representing the ident-map")
                               (s/one {s/Any s/Any} "The map of IDs in ident-maps to the actual IDs in the triples")]
  "Converts a single map to triples for an ID'ed map"
  ([graph :- GraphType
    j :- EntityMap]
   (ident-map->triples graph j {}))
  ([graph :- GraphType
    j :- EntityMap
    id-map :- {s/Any s/Any}]
   (binding [*current-graph* graph
             *id-map* (volatile! id-map)]
     (ident-map->triples j)))
  ([j :- EntityMap]
   (let [[node-ref triples] (map->triples j)]
     [(doall
       (if (:db/ident j)
         triples
         (concat [[node-ref :db/ident (name-for node-ref)] [node-ref :tg/entity true]] triples)))
      @*id-map*])))


(s/defn entities->triples :- [Triple]
  "Converts objects into a sequence of triples."
  ([graph :- GraphType
    entities :- [EntityMap]]
   (entities->triples graph entities {}))
  ([graph :- GraphType
    entities :- [EntityMap]
    id-map :- {s/Any s/Any}]
   (binding [*current-graph* graph
             *id-map* (volatile! id-map)]
     (let [triples-ids (map ident-map->triples entities)]
       (doall (mapcat first triples-ids))))))  ;; drop the id maps


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
  (binding [*current-graph* graph]
    (let [pvs (reader/property-values graph node-ref)
          old-node (reader/pairs->struct graph pvs)
          to-remove (remove (fn [[k v]] (if-let [newv (get entity k)] (= v newv))) old-node)
          pvs-to-remove (filter (comp (set (map first to-remove)) first) pvs)
          triples-to-remove (mapcat (partial existing-triples graph node-ref) pvs-to-remove)

          to-add (remove (fn [[k v]] (when-let [new-val (get old-node k)] (= new-val v))) entity)
          triples-to-add (doall (mapcat (partial property-vals node-ref) to-add))]
      [triples-to-add triples-to-remove])))
