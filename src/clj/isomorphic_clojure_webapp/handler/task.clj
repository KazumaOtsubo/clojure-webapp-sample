(ns isomorphic-clojure-webapp.handler.task
  (:require [clojure.data.json :as json]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defmethod ig/init-key ::index [_ {:keys [db]}]
  (fn [req]
    {:status 200
     :headers {"content-type" "application/json"}
     :body (json/write-str (jdbc/execute! (:datasource (:spec db)) ["select * from tasks"]))}))