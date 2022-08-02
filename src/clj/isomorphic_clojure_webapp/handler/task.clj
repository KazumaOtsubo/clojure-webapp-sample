(ns isomorphic-clojure-webapp.handler.task
  (:require [clojure.data.json :as json]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defmethod ig/init-key ::index [_ {:keys [db]}]
  (fn [_]
    {:status 200
     :headers {"content-type" "application/json"}
     :body (json/write-str (jdbc/execute! (:datasource (:spec db)) ["select * from tasks"]))}))

(defmethod ig/init-key ::add-task [_ {:keys [db]}]
  (fn [{:keys [params]}]
    {:status 200
     :body (let [title (:title params)]
             (jdbc/execute! (:datasource (:spec db))
                            [(str "insert into tasks (title) values ('" title "')")]))}))