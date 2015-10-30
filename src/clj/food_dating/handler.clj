(ns food-dating.handler
  (:require [ring.middleware.defaults :refer [api-defaults site-defaults wrap-defaults]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [environ.core :refer [env]]
            [schema.core :as s]
            [compojure.route :as route]
            [compojure.api.middleware :refer [wrap-components]]
            [food-dating.backend :as be]))

(def home-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
      (if (env :dev) 
                    (include-css "css/site.css" "css/bootstrap.css" "css/bootstrap-theme.css" "css/forms.css") 
                    (include-css "css/site.min.css" "css/bootstrap.min.css" "css/bootstrap-theme.min.css"))]
    [:body
     [:div#app
      [:h3 "Loading... please wait."]]
     (include-js "js/app.js")]]))

(defroutes* html-routes
  (GET* "/" [] home-page)
  (route/resources "/")
  (route/not-found "Not Found"))

(defn add! [user]
  (println "Posted a user")
  {:login "Lello" :password "Arena"}
  )

(defn get-user [id]
  (println (str "Returning user with id " id))
  (be/get-user id)
  )

(defn add-user-food! [user-id food]
  (be/add-user-food user-id food))

(s/defschema User {:login String
                   :password String})

(s/defschema Food {:name String
                   (s/optional-key :ingredients) [String] })

(defapi api-app 
  (swagger-ui "/api-ui" 
              :swagger-docs "/api-docs")
  (swagger-docs "/api-docs"
     {:info {:version "1.0.0"
            :title "Foodie API"
            :description "the description"
            :termsOfService "http://www.metosin.fi"
            :contact {:name "My API Team"
                      :email "foo@example.com"
                      :url "http://www.metosin.fi"}
            :license {:name "Eclipse Public License"}}})

  (GET* "/" [] home-page)

  (context* "/user" []
    :tags ["user"]
    (GET* "/:id" []
      :return User
      :summary "Returns a JSON object with the details of the specified user"
      :path-params [id :- Long]
      (ok (get-user id)))
    (POST* "/" []
      :return User
      :summary "Create a new user and return the newly created user entity"
      :body [user User]
      (ok (add! user)))
    (POST* "/:user-id/food" []
      :return Food
      :summary "Add a new food to the specified user"
      :path-params [user-id :- Long]
      :body [food Food]
      (ok (add-user-food! user-id food)))))

(def app
  ;(let [ defaults (assoc-in site-defaults [:security :anti-forgery] false)
  ;      handler (wrap-defaults #'api-app api-defaults)]
    (if (env :dev) (-> api-app wrap-exceptions wrap-reload) api-app))
;)
