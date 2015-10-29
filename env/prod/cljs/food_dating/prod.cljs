(ns food-dating.prod
  (:require [food-dating.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
