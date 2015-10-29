(ns food-dating.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.debug :as d]      
              [reagent-forms.core :as forms :refer [bind-fields]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; Views

(enable-console-print!)

(def dishes-db ["pasta" "riso" "carne"])

(defn find-dish [val]
  ;select dishes where name like %val%
  (let [found (filter #(-> % (.toLowerCase %) (.indexOf val) (> -1)) dishes-db)]
    (if (seq found)
        found
        [val])
  ))

;once I enter a food (dish? ingredient?) this should be added to my profile.
;Then I can implementa  search users by food

(defn enter-food [doc]
  [:div  
            {:field :typeahead 
             :id :ta
             :placeholder "Enter a dish"
             :data-source find-dish 
             :input-class     "form-control"
             :list-class      "typeahead-list"
             :item-class      "typeahead-item"
             :highlight-class "highlighted"
             :clear-on-focus? false
             :choice-fn (fn [choice] 
                          (when-not (some #{choice} (@doc :my-dishes))
                            (swap! doc assoc-in [:my-dishes] (conj (@doc :my-dishes) choice))))
            }]
  )

(defn home-page []
  (let [doc (atom {:my-dishes ["Panna"]})]
   (fn []
  [:div [:h2 "Your Food Preferences"]
   [:div [:p (str "My dishes: " @doc)]]
   [:div 
       [bind-fields
          (enter-food doc)
            doc
            ]]
   ])))

(defn about-page []
  [:div [:h2 "About food-dating"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
