(ns food-dating.mongo
 (:require [monger.core :as mg]))

(defn get-db []
	(let [conn (mg/connect)
      	  db   (mg/get-db conn "food-dating")]

      db))

(defn disconnect []
	(let [conn (mg/connect)]
  	  (mg/disconnect conn)))