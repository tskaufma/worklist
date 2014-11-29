(ns worklist-rest.core.util)

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn now-ts [] (java.sql.Timestamp. (.getTime (java.util.Date.))))
