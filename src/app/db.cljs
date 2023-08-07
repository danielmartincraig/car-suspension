(ns app.db
  (:require 
   [emmy.matrix :as matrix :refer [s->m]]))

(def laplacian-matrix
  (matrix/by-rows [6.3 -3.7 -2.6 0 0]
                  [-8.9 10.1 0 -1.2 0]
                  [0 0 4.2 -1.9 -2.3]
                  [0 0 0 0 0]
                  [-4.4 0 0 -2.7 7.1]))

(def default-db
  {:todos (sorted-map-by >)
   :springs laplacian-matrix})
