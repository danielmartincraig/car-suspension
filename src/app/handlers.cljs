(ns app.handlers
  (:require [re-frame.core :as rf]
            [app.fx :as fx]))

(def load-app-state (rf/inject-cofx :store/app-state "car-suspension/app-state"))
(def store-app-state (fx/store-app-state "car-suspension/app-state"))

(rf/reg-event-fx :app/init-db
  [load-app-state]
  (fn [{:store/keys [app-state]} [_ default-db]]
    {:db (update default-db :app-state into app-state)}))

(rf/reg-event-db :displacement/update-displacement
                 [store-app-state]
                 (fn [db [_ i new-displacement]]
                   (if (<= (abs new-displacement) 10)
                     (assoc-in db [:app-state :displacement 0 i] new-displacement)
                     db)))

(rf/reg-event-db :displacement/reset-displacements
                 [store-app-state]
                 (fn [db _]
                   (assoc-in db [:app-state :displacement] [[0 0 0]])))