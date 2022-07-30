(ns isomorphic-clojure-webapp.ui.root
  (:require
   [rum.core :refer [defc use-state use-effect!]]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<! go]]))

(defc task-list
  [tasks]
  (if (nil? tasks)
    [:h2 "読み込み中"] 
    [:ul
     (for [task tasks]
       [:li {:key (:id task)}
        (:title task)])]))

(defc root
  []
  (let [[tasks set-tasks!] (use-state nil)
        _ (use-effect! (fn []
                         (go
                           (let [response (<! (http/get "http://localhost:3000/tasks"))]
                             (set-tasks! (:body response))))
                   ;; 行う処理
                   ;; クリーンアップ用の関数（必要ないので、何もしない無名関数を返す）
                         #())
                 ;; どのデータが変更された時に実行するか
                       [])]
    [:div
     [:h1 "Clojure/Script Todo List"]
     (task-list tasks)]))

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
      (get m "age") ;; 通る。getという関数は、引数にマップとキーを受け取って、対応する値を返す。
      )))