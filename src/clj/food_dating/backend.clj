(ns food-dating.backend
	(:require [yesql.core :refer [defquery defqueries]]))

(def db-spec {:classname "org.postgresql.Driver"
			  :subprotocol "postgresql"
			  :subname "//localhost:5432/food_dating"
			  :user "food"
			  :password "food"})

(defqueries "user_queries.sql"
	{:connection db-spec})

(defn get-user [id]
	(let [res (first (user-by-id {:? [id]}))
		  ]
		  {:login (:login res) :password (:password res)}
		  )
	)