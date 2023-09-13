(ns GameOfLife 
  (:require [clojure.set :refer :all])
  (:require [clojure.test :refer :all]))
  

(defn neighbors-of [[x y]]
  (into #{}
        (remove (fn [cell] (= [x y] cell)) (for [i [-1 0 1] j [-1 0 1]] [(- x i) (- y j)]))))

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

(defn board-to-string [living]
  (for [i [living]] (apply str i))
  (clojure.string/join "\n" (let [minX (apply min (map first living))
                                  maxX (apply max (map first living))
                                  minY (apply min (map second living))
                                  maxY (apply max (map second living))]
                              (for [i (range minY (inc maxY))]
                                (apply str (for [j (range minX (inc maxX))] (if (contains? living [j i]) "#" "-")))))))

(defn string-to-board [board]
  (into #{} (remove nil? (let [rows (clojure.string/split-lines board)]
                           (for [i (range 0 (count rows))
                                 j (range 0 (count (first rows)))] (if (= (nth (nth rows i) j) \#) [j i]))))))





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

(deftest TestStringtoBoard
  (is (= "#-----\n------\n-##---\n---#--\n------\n------\n------\n-----#" 
         (board-to-string #{[2 2] [3 3] [5 7] [1 2] [0 0]}))))

(deftest TestBoardtoString
  (is (= #{[2 2] [3 3] [5 7] [1 2] [0 0]} 
         (string-to-board "#-----\n------\n-##---\n---#--\n------\n------\n------\n-----#"))))

(board-to-string #{[0 0] [1 0] [2 0] [1 1] [2 1]})

(println (board-to-string #{[0 0] [0 1] [1 0] [1 1] [-1 0]}))
(board-to-string #{[0 1] [1 7] [8 7] [5 9] [10 7] [5 3] [-8 -4]})

(string-to-board "###\n-##")

(neighbors-of [0 0])
(living-neighbors [0 0] #{[0 0] [0 1] [1 0]})
(will-live [0 0] #{[0 0] [0 1] [1 0] [1 1] [-1 0]})
(will-live [1 1] #{[1 2] [1 0] [0 1]})
(will-live [1 1] #{[1 2] [1 0]})
(will-live [1 1] #{[1 1] [1 0] [0 1]})
(next-gen #{[1 1] [2 2] [2 1]})
(next-gen #{[1 1] [1 2] [1 3] [2 1] [3 3]})
