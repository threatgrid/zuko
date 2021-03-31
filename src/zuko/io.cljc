(ns ^{:doc "Some simple portable io operations"
      :author "Paula Gearon"}
    zuko.io
    (:refer-clojure :exclude [spit slurp])
    #?(:clj (:import [java.io File])))

#?(:cljs (def fs (js* "(() => {try { return require('fs'); } catch (err) { return null; }})()")))

(def slurp
  #?(:clj clojure.core/slurp
     :cljs
     (if fs
       (fn [source]
         (fs.readFileSync source "utf8"))
       (fn [source]
         (let [s js/Window.localStorage]
           (.getItem s source))))))

(def spit
  #?(:clj clojure.core/spit
     :cljs
     (if fs
       (fn [dest data]
         (fs.writeFileSync dest data "utf8"))
       (fn [dest data]
         (let [s js/Window.localStorage]
           (.setItem s dest data))))))

(def exists
  #?(:clj
     (fn [path]
       (let [f (File. path)]
         (.exists f)))
     :cljs
     (if fs
       (fn [path]
         (fs.existsSync path))
       (fn [path]
         (let [s js/Window.localStorage]
           (not (nil? (.getItem s path))))))))
(def rm
  #?(:clj
     (fn [path]
       (let [f (File. path)]
         (.delete f)))
     :cljs
     (if fs
       (fn [path]
         (when (exists path)
           (fs.unlinkSync path)
           true))
       (fn [path]
         (let [s js/Window.localStorage]
           (when (.getItem s path)
             (.removeItem s path)
             true))))))

