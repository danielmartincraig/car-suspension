(ns app.handlers
  (:require [re-frame.core :as rf]
            [app.fx :as fx]
            [app.db :as db]))

(def load-app-state (rf/inject-cofx :store/app-state "car-suspension/app-state"))
(def store-app-state (fx/store-app-state "car-suspension/app-state"))

(rf/reg-event-fx :app/init-db
  [load-app-state]
  (fn [{:store/keys [app-state]} [_ default-db]]
    {:db (update default-db :app-state into app-state)}))

(rf/reg-event-db :displacement/update-displacement
                 [store-app-state]
                 (fn [db [_ i new-displacement]]
                   (if (<= (abs new-displacement) 15)
                     (assoc-in db [:app-state :displacement i 0] new-displacement)
                     db)))

(rf/reg-event-db :time/update-time
                 [store-app-state]
                 (fn [db [_ new-time]]
                   (if (>= new-time 0) 
                     (assoc-in db [:app-state :time] new-time)
                     db)))

(rf/reg-event-db :displacement/reset-app-db
                 [store-app-state]
                 (fn [db _]
                   db/default-db))