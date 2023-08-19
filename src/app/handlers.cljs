(ns app.handlers
  (:require [re-frame.core :as rf]
            [app.fx :as fx]))

(def load-todos (rf/inject-cofx :store/todos "uix-starter/todos"))
(def load-app-state (rf/inject-cofx :store/app-state "car-suspension/app-state"))
(def store-todos (fx/store-todos "uix-starter/todos"))
(def store-app-state (fx/store-app-state "car-suspension/app-state"))

(rf/reg-event-fx :app/init-db
  [load-app-state]
  (fn [{:store/keys [app-state]} [_ default-db]]
    {:db (update default-db :app-state into app-state)}))

(rf/reg-event-fx :todo/add
  [(rf/inject-cofx :time/now) store-todos]
  (fn [{:keys [db]
        :time/keys [now]}
       [_ todo]]
    {:db (assoc-in db [:todos now] todo)}))

(rf/reg-event-db :todo/remove
  [store-todos]
  (fn [db [_ created-at]]
    (update db :todos dissoc created-at)))

(rf/reg-event-db :todo/set-text
  [store-todos]
  (fn [db [_ created-at text]]
    (assoc-in db [:todos created-at :text] text)))

(rf/reg-event-db :todo/toggle-status
  [store-todos]
  (fn [db [_ created-at]]
    (update-in db [:todos created-at :status] {:unresolved :resolved
                                               :resolved :unresolved})))

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