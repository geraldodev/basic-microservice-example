(ns basic-microservice-example.protocols.storage-client
  (:require [schema.core :as s]))

(defprotocol StorageClient
  "Protocol for simple storage mechanism; simple but not practical in any way"
  (read-all   [storage]           "Return the entire contents of storage")
  (put!       [storage update-fn] "Mutate the storage with the provided function")
  (clear-all! [storage]           "Clear the storage"))

(def IStorageClient (s/protocol StorageClient))
