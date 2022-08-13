(ns isomorphic-clojure-webapp.handler.task
  (:require [clojure.data.json :as json]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [java-time :as jt]))

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

(defmethod ig/init-key ::check [_ {:keys [db]}]
  (fn [{:keys [params]}]
    {:status 200
     :body (let [id (:id params)]
            (jdbc/execute! (:datasource (:spec db))
                            ["update tasks set completed_at = ? where id = ?" (jt/local-date (jt/sql-date)) id]))}))

(defmethod ig/init-key ::delete [_ {:keys [db]}]
  (fn [{:keys [params]}]
    {:status 200
     :body (let [id (:id params)]
             (jdbc/execute! (:datasource (:spec db)) ["delete from tasks where id = ?" id]))}))