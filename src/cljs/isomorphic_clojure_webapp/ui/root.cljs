(ns isomorphic-clojure-webapp.ui.root
  (:require
   [rum.core :refer [defc use-state use-effect!]]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<! go]]))

(def all-task (atom ()))

(defn add-todo
  [title]
  (go
    (let [response (<! (http/post (str "http://localhost:3000/tasks?title=" title)))]
      (if (not (= 200 (:status response)))
        (println "ERROR OCCURED")))))

(defn check-todo
  [id]
  (go
    (let [response (<! (http/post (str "http://localhost:3000/check?id=" id)))]
      (if (not (= 200 (:status response)))
        (println "ERROR OCCURED")))
    ))

(defn delete-todo
  [id]
  (go
    (let [response (<! (http/post (str "http://localhost:3000/delete?id=" id)))]
      (if (not (= 200 (:status response)))
        (println "ERROR OCCURED")))))


(defn get-all-tasks
  []
  (go
    (let [response (<! (http/get "http://localhost:3000/tasks"))] 
      (:body response))))

(defc check-box
  [on-check id]
  [:input {:type "checkbox"
           :name "check"
           :id "check_m"
           :on-change  (fn[] (on-check id))}])

(defc delete-button
  [on-delete id]
  [:input {:type "text"
           :name "delete"
           :id "delete_m"}]
  [:button {:type "button"
            :on-click (fn [] (on-delete id))}
   "DELETE"])

(defc each-task
  [on-check on-delete task]
  (let [id (:id task)
        title (:title task)
        completed_at (:completed_at task)] 
    [[:td (check-box on-check id)] 
     [:td title] 
     [:td (if (nil? completed_at)["not yet"][completed_at])] 
     [:td (delete-button on-delete id)]]))

(defc task-list
  [on-check on-delete tasks]
  (if (nil? tasks)
    [:h2 "読み込み中"] 
    [:table
     (for [task tasks]
       [:tr {:key (:id task)
             :list-style "none"}
        (each-task on-check on-delete task)])]))

(defc input-todo
  [on-create]
  [:div
     [:form {:action "" :method "post"}
    [:input {:type "text"
             :name "title"
             :id "input_m"}]
              [:button {:type "button"
              :on-click on-create}  
     "ADD"]]])

(defc root
  []
  (let [[tasks set-tasks!] (use-state nil)
        fetch-tasks (fn []
                    (go
                      (let [response (<! (http/get "http://localhost:3000/tasks"))]
                        (set-tasks! (:body response))))
                   ;; 行う処理
                   ;; クリーンアップ用の関数（必要ないので、何もしない無名関数を返す）
                    #())
        on-create (fn []
                    (go (<! (add-todo (.-value (.getElementById js/document "input_m"))))
                        (fetch-tasks)))
        on-check (fn [id]
                    (go (<! (check-todo id))
                        (fetch-tasks)))
        on-delete (fn [id]
                    (go (<! (delete-todo id))
                        (fetch-tasks)))
        _ (use-effect! fetch-tasks
                       ;; どのデータが変更された時に実行するか
                       [])]
    [:div
     [:h1 "Clojure/Script Todo List"]
     (input-todo on-create)
     (task-list on-check on-delete tasks)]))

(comment
  "学習材料"
  (defn pare-test
    []
  ;; 普通のカッコは、関数呼び出し
    (str 1 2 3)
  ;; カギカッコは、配列（vector）を示す 
    [1 2 3]
  ;; ナミカッコは、辞書？マップ？を示す
    {:a 1 :b 2 :c 3})


  (defn str-and-keyword
    []
    (let [m {:id 1 :name "John Doe" "age" 26}]
   ;; 通る。キーワードは関数チックに動く。clojure.lang.Keywordはclojure.lang.IFnを実装している。
      (:id m)
    ;; ("age" m) ;; 通らない。java.lang.Stringはclojure.lang.IFnを実装していない。
      (get m "age")))) ;; 通る。getという関数は、引数にマップとキーを受け取って、対応する値を返す。
      