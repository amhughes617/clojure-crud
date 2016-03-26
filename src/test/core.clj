(ns test.core
  (:require [clojure.java.jdbc :as jdbc])
  (:gen-class))

 
(def spec
   {:classname   "org.h2.Driver"
    :subprotocol "h2"
    :subname     "localhost:5432/mem:./test"
    :user        "sa"
    :password    ""})

(defn table-exists? [table-name]
  (try
    (jdbc/query spec [(str "SELECT COUNT(*) FROM " table-name)])
    true
    (catch Exception _ false)))

(defn create-users-table! []
 (when-not (table-exists? "joins") 
  (jdbc/db-do-commands spec 
    (jdbc/create-table-ddl "users"
       [:id "BIGINT" "PRIMARY KEY AUTO_INCREMENT"]
       [:name "VARCHAR"]
       [:password "VARCHAR"]
       [:parent "BOOLEAN"]
       [:marks "BIGINT"]
       [:stars "BIGINT"]))))
(defn create-offspring-join! []
 (when-not (table-exists? "joins")
  (jdbc/db-do-commands spec
   (jdbc/create-table-ddl "joins"
    [:id "BIGINT" "PRIMARY KEY AUTO_INCREMENT"]
    [:parent "BIGINT"]
    [:offspring "BIGINT"]))))
(defn create-consequences-table! []
 (when-not (table-exists? "joins") 
  (jdbc/db-do-commands spec
   (jdbc/create-table-ddl "consequences"
     []))))

(defn populate-table! [table-name & rows]
  (doseq [r rows]
   (jdbc/insert! spec table-name r)))

(defn -main []
                        
  (create-users-table!)
  (create-offspring-join!)
  (populate-table! "users"
                   {:name "Mom"
                    :password "mompass"
                    :parent true
                    :marks nil
                    :stars nil} 
                   {:name "George"
                    :password "134"
                    :parent false
                    :marks 7
                    :stars 3})
  (populate-table! "joins"
                   {:offspring 2})
  (println (jdbc/query spec 
             ["SELECT * FROM users INNER JOIN joins ON joins.offspring = users.id WHERE name = ?"
              "George"])))
       
       
