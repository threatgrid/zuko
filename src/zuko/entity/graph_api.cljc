(ns ^{:doc "Describes simple functions for connecting to a graph"
      :author "Paula Gearon"}
  zuko.entity.graph-api)

(defprotocol SimpleGraphAPI
  (data-attribute [graph data] "Returns a keyword used for an attribute refering to the data")
  (container-attribute [graph data] "Returns a keyword used for an attribute that contains the data")
  (new-node [graph] "A function that returns a new node object for the graph")
  (node-label [graph node-id] "Converts an identifier like a string or number into a keyword for a node")
  (resolve-pattern [graph pattern] "resolves a simple triple pattern against the graph edges"))

