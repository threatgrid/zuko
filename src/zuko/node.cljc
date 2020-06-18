(ns ^{:doc "Describes simple functions for describing nodes in a graph"
      :author "Paula Gearon"}
  zuko.node)

(def tg-ns "tg")

(defprotocol NodeAPI
  (data-attribute [graph data] "Returns a keyword used for an attribute refering to the data")
  (container-attribute [graph data] "Returns a keyword used for an attribute that contains the data")
  (new-node [graph] "A function that returns a new node object for the graph")
  (node-id [graph n] "Returns a id for a node. Numbers are good")
  (node-type? [graph a n] "Tests if the argument is a valid node type for this graph, given an attribute")
  (find-triple [graph pattern] "resolves a simple triple pattern against the graph edges"))

(defn node-label
  "Returns a keyword label for a node"
  [s n]
  (keyword "tg" (str "id-" (node-id s n))))
