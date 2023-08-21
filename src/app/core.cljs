(ns app.core
  (:require
   [cljs.spec.alpha :as s]
   [uix.core :as uix :refer [defui $]]
   [uix.dom]
   [app.hooks :as hooks]
   [app.subs]
   [app.handlers]
   [app.fx]
   [app.db]
   [re-frame.core :as rf]
   [clojure.string :as str]))

(defn color-fn [force]
  (cond (<= (abs force) 0) "#ffffff"
        (<= (abs force) 1) "#ffe6e6"
        (<= (abs force) 2) "#ffcccc"
        (<= (abs force) 3) "#ffb3b3"
        (<= (abs force) 4) "#ff9999"
        (<= (abs force) 5) "#ff8080"
        (<= (abs force) 6) "#ff6666"
        (<= (abs force) 7) "#ff4d4d"
        (<= (abs force) 8) "#ff3333"
        (<= (abs force) 9) "#ff1a1a"
        (<= (abs force) 10) "#ff0000"
        (<= (abs force) 11) "#e60000"
        (<= (abs force) 12) "#cc0000"
        (<= (abs force) 13) "#b30000"
        (<= (abs force) 14) "#990000"
        (<= (abs force) 15) "#800000"
        (<= (abs force) 16) "#660000"
        (<= (abs force) 17) "#4d0000"
        (<= (abs force) 18) "#330000"
        (<= (abs force) 19) "#1a0000"
        :else "#1a0000"))

(defui spring-network-view []
  (let [x1 (hooks/use-subscribe [:app/displacement 0])
        x2 (hooks/use-subscribe [:app/displacement 1])
        x3 (hooks/use-subscribe [:app/displacement 2])     
        f1 (hooks/use-subscribe [:app/force 0])
        f2 (hooks/use-subscribe [:app/force 1])
        f3 (hooks/use-subscribe [:app/force 2])]
    ($ :svg
       ($ :path {:stroke "black"
                 :stroke-width 10
                 :d (str/join " " ["M" (+ 100 x1) 25 "L" (+ 250 x2) 25])})
       ($ :path {:stroke "black"
                 :stroke-width 10
                 :d (str/join " " ["M" (+ 250 x2) 25 "L" (+ 350 x3) 25])})
       ($ :rect {:x (+ 100 x1) :y 0 :width 50 :height 50 :style {:fill (color-fn f1) :stroke-width 3 :stroke "black"}})
       ($ :rect {:x (+ 200 x2) :y 0 :width 50 :height 50 :style {:fill (color-fn f2) :stroke-width 3 :stroke "black"}})
       ($ :rect {:x (+ 300 x3) :y 0 :width 50 :height 50 :style {:fill (color-fn f3) :stroke-width 3 :stroke "black"}})
       ($ :text {:x (+ 102 x1) :y 80 :font-family "Verdana" :font-size 26} f1)
       ($ :text {:x (+ 202 x2) :y 80 :font-family "Verdana" :font-size 26} f2)
       ($ :text {:x (+ 302 x3) :y 80 :font-family "Verdana" :font-size 26} f3))))

(defui reset-displacements-button []
  ($ :div ($ :button
             {:on-click #(rf/dispatch [:displacement/reset-displacements])}
             "Reset")))
  

(defui header []
  ($ :header.app-header
    ($ :div {:width 32} "Network of Springs" )))

(defui footer []
  ($ :footer.app-footer
    ($ :small "made by Daniel Craig")))

(defui displacement-field [{:keys [on-edit-displacement i]}]
  (let [displacement (hooks/use-subscribe [:app/displacement i])]
    ($ :div
       ($ :div "Displacement " i)
       ($ :input
          {:value displacement
           :type :number
           :placeholder 0
           :on-change (fn [^js e]
                        (on-edit-displacement (int (.. e -target -value))))}))))

(defui app []
  (let [todos (hooks/use-subscribe [:app/todos])]
    ($ :.app
       ($ header)
       ($ spring-network-view)
       ($ displacement-field {:i 0 :on-edit-displacement #(rf/dispatch [:displacement/update-displacement 0 %])})
       ($ displacement-field {:i 1 :on-edit-displacement #(rf/dispatch [:displacement/update-displacement 1 %])})
       ($ displacement-field {:i 2 :on-edit-displacement #(rf/dispatch [:displacement/update-displacement 2 %])})
       ($ reset-displacements-button)
       ($ footer))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (rf/dispatch-sync [:app/init-db app.db/default-db])
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (render))
