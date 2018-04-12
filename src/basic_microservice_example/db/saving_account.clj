(ns basic-microservice-example.db.saving-account
  (:require [schema.core :as s]
            [basic-microservice-example.protocols.storage-client :as storage-client]))

(defn build-assocer [account]
  (fn [m]
    (do (println "&&&&\t" m)
    (assoc m (:id account) account))))

(defn add-account! [account storage]
  (storage-client/put! storage
                       (build-assocer account)))

(defn remove-account! [account-id storage]
  (storage-client/put! storage #(dissoc % account-id)))

(defn accounts [storage]
  (storage-client/read-all storage))

(defn account [account-id storage]
  (get (storage-client/read-all storage) account-id))
