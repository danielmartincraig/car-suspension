(ns app.fx
  (:require
    [clojure.edn :as edn]
    [re-frame.core :as rf]))

(rf/reg-cofx :store/app-state
             (fn [cofx store-key]
               (let [app-state (edn/read-string (js/localStorage.getItem store-key))]
                 (rf/console :log (str "Found app state " app-state))
                 (assoc cofx :store/app-state app-state))))

(defn store-app-state [store-key]
  (rf/->interceptor
   :id :store/set-app-state
   :after (fn [context]
            (js/localStorage.setItem store-key (-> context :effects :db :app-state str))
            context)))