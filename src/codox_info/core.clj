(ns codox-info.core
  (:require [clojure.pprint :refer [cl-format]]
            [clojure.string :refer [lower-case trim-newline]]
            [clojure.java.io :refer [file writer]]))

(defn write-definition [{:keys [name type doc arglists members dynamic] :as var} & [prefix]]
  (let [prefix (or prefix "")]
    (cl-format true (str prefix "~A~:[~; ~:*~A~]~:[~; dynamic~]~%~{" prefix "(~{~A~^ ~})~%~}~:[~;" prefix "~:*~A~&~]~%")
               name (when-not (= :var type) (subs (str type) 1)) dynamic (map #(cons name %) arglists) doc)
    (doseq [member members]
      (write-definition member (str prefix "  ")))))

(defn write-node-first-line [title name & [up next prev]]
  (cl-format true "~%~A~:[~;, Node: ~:*~A~]~:[~;, Next: ~:*~A~]~:[~;, Prev: ~:*~A~]~:[~;, Up: ~:*~A~]~%~%"
             title name next prev up))

(defn write-with-underline [text underline-char]
  (cl-format true (str "~A~&~:*~{" underline-char "~*~}~%~%") text))

(defn write-menu-entry [node text description]
  (cl-format true "* ~A:~:[:~; ~A~] ~A~%" node (when-not (= text node) text) description))

(defn write-menu [entries]
  (println "* Menu:")
  (println "")
  (doseq [{:keys [node text description]} entries]
    (write-menu-entry node text description)))

(defn write-to-info
  "Take raw documentation info and turn it into an INFO file."
  [{:keys [output-dir name description namespaces] :as project}]
  (with-open [out (writer (file output-dir (str (lower-case name) ".info")))]
    (binding [*out* out]
      (cl-format true "This is ~(~A~).info, produced by codox/codox-info.~%INFO-DIR-SECTION Clojure API Documentation~%START-INFO-DIR-ENTRY~%* ~:*~A: (~(~:*~A~)).           ~A~%END-INFO-DIR-ENTRY~%~%" name description)
      (write-node-first-line (str (lower-case name) ".info") "Top" "(dir)")
      (write-with-underline (str name) "*")
      (cl-format true "~A~&~%" description)
      (write-menu (map #(hash-map :node (:name %) :description (:doc %))
                       (sort-by :name namespaces)))
      (doseq [[{prev name} {:keys [doc name publics] :as namespace} {next :name}]
              (partition 3 1 (concat [nil] namespaces [nil]))]
        (write-node-first-line (str (lower-case (:name project)) ".info") name "Top" prev next)
        (write-with-underline (str name) "=")
        (cl-format true "~A~&~%" doc)
        (write-with-underline "Members:" ".")
        (doseq [public (sort-by :name publics)]
          (write-definition public))))))
