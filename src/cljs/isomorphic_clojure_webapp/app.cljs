(ns isomorphic-clojure-webapp.app
  (:require
    [isomorphic-clojure-webapp.ui.root :refer [root]]
    [rum.core :refer [mount]]))


;; shadow-cljs.ednから直接呼ばれるため.
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}

(defn ^:dev/after-load start-app
  []
  (mount (root) (js/document.getElementById "app")))

(defn init
  []
  (start-app))
