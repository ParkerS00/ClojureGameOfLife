(ns GameOfLife 
  (:require [clojure.set :refer :all])
  (:require [clojure.test :refer :all])
  (:require [quil.core :as q]
            [quil.middleware :as m]))
  

(defn neighbors-of [[x y]]
  (into #{}
        (remove (fn [cell] (= [x y] cell)) (for [i [-1 0 1] j [-1 0 1]] [(- x i) (- y j)]))))

(defn neighbors-of-memo [[x y]]
  (memoize neighbors-of))

(defn living-neighbors [[x y] living]
  (def neighbors (neighbors-of [x y]))
  (count (remove nil? (for [i living] (neighbors i)))))

(defn will-live [[x y] living]
  (def aliveCount (living-neighbors [x y] living))
  (if (or (> aliveCount 3) (< aliveCount 2)) false
      (if (not= (living [x y]) nil) true
          (if (= aliveCount 3) true false))))

(defn next-gen [living]
  (into #{} (filter (fn [n] (will-live n living))
                    (reduce clojure.set/union (into #{} (map neighbors-of living))))))

(defn tests [opts]
  (run-all-tests))

(deftest TestNeighbors [] 
  (is (= #{[2 2] [0 0] [1 0] [0 2] [2 0] [2 1] [1 2] [0 1]} (neighbors-of [1 1]))))

(deftest TestingLivingNeighbors []
  (is (= 3 (living-neighbors [1 1] #{[2 2] [3 3] [5 7] [1 2] [0 0]}))))

(deftest TestingWillLive
  (is (= true (will-live [1 1] #{[2 2] [3 3] [5 7] [1 2] [0 0]}))))

(deftest TestNextGen
  (is (= #{[2 2] [2 3] [1 1]} 
         (next-gen #{[2 2] [3 3] [5 7] [1 2] [0 0]}))))

(defn setup []
  (q/frame-rate 20)                    
  (q/background 200)
  (into #{} (remove nil? (for [i (range 20) j (range 20)] (if (< (rand 1) 0.3) [i j])))))                 
                                      
(defn draw [state]
  (q/background 255 255 255)
  (doseq [i state] ((q/fill 0 0 0)(q/rect (* (first i) 10) (* (second i) 10) 10 10))))  

(q/defsketch example                 
  :title "Conway Game Of Life"     
  :setup #'setup                        
  :draw #'draw                          
  :size [1000 1000]
  :update #'next-gen
  :middleware [m/fun-mode])

(defn -main [opts])