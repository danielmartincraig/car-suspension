(ns app.subs
  (:require [re-frame.core :as rf]
            [emmy.env :as emmy]
            [emmy.matrix :as matrix]))

(def friction 0.75)
(def block-mass 40)

(defn calculate-forces
  [springs displacements]
  (emmy/- (emmy/* springs
                  displacements)))

(defn calculate-momentum
  [momentum forces]
  (let [acceleration (emmy/* (emmy// forces block-mass) friction)]
    (emmy/+ momentum acceleration)))

(defn calculate-displacements 
  [displacements momentum]
  (emmy/+ displacements momentum))

(defn step 
  [{:keys [springs displacements momentum]}]
   (let [forces            (calculate-forces springs displacements)
         new-displacements (calculate-displacements displacements momentum)
         new-momentum      (calculate-momentum momentum forces)]
     {:springs springs 
      :displacements new-displacements 
      :momentum new-momentum}))

(rf/reg-sub :app/todos
            (fn [db _]
              (:todos db)))

(rf/reg-sub :app/app-state
            (fn [db _]
              (:app-state db)))

(rf/reg-sub :app/springs
            :<- [:app/app-state]
            (fn [app-state _]
              (apply matrix/by-rows (:springs app-state))))

(rf/reg-sub :app/initial-displacements
            :<- [:app/app-state]
            (fn [app-state _]
              (apply matrix/by-rows (:displacement app-state))))

(rf/reg-sub :app/initial-momentum
            :<- [:app/app-state]
            (fn [app-state _]
              (apply matrix/by-rows (:initial-momentum app-state))))

(rf/reg-sub :app/time
            :<- [:app/app-state]
            (fn [app-state _]
              (:time app-state)))

(rf/reg-sub :app/forces
            :<- [:app/springs]
            :<- [:app/initial-displacements]
            (fn [[springs displacements] _]
              (emmy/- (emmy/* springs displacements))))

(rf/reg-sub :app/initial-displacement
            :<- [:app/initial-displacements]
            (fn [displacements [_ i]]
              (get-in displacements [i 0])))

(rf/reg-sub :app/force
            :<- [:app/forces]
            (fn [forces [_ i]]
              (get-in forces [i 0])))

(rf/reg-sub :app/simulation
            :<- [:app/springs]
            :<- [:app/initial-displacements]
            :<- [:app/initial-momentum]
  (fn [[springs displacements momentum] _]
    (iterate step {:springs springs
                   :displacements displacements
                   :momentum momentum})))

(rf/reg-sub :app/simulation-at-time
            :<- [:app/simulation]
            :<- [:app/time]
            (fn [[simulation time] _]
              (nth simulation time)))

(rf/reg-sub :app/simulation-displacements
            :<- [:app/simulation-at-time]
            (fn [simulation-at-time _]
              (:displacements simulation-at-time)))

(rf/reg-sub :app/simulation-displacement
            :<- [:app/simulation-displacements]
            (fn [simulation-displacements [_ i]]
              (get-in simulation-displacements [i 0])))

(rf/reg-sub :app/simulation-forces
            :<- [:app/springs]
            :<- [:app/simulation-displacements]
            (fn [[springs simulation-displacements] _]
              (calculate-forces springs simulation-displacements)))

(rf/reg-sub :app/simulation-force
            :<- [:app/simulation-forces]
            (fn [simulation-forces [_ i]]
              (get-in simulation-forces [i 0])))
