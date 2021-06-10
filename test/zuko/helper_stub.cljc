(ns zuko.helper-stub
  (:require [zuko.logging :as log :refer-macros [info]]
            #?(:clj  [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

(defn test-logger
  [data]
  (log/info data))
