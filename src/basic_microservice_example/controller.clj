(ns basic-microservice-example.controller
  (:require [basic-microservice-example.protocols.http-client :as http-client]
            [basic-microservice-example.logic :as logic]
            [basic-microservice-example.db.saving-account :as db.saving-account]))

(defn get-customer! [customer-name http]
  (let [url  (str "http://customer-service.com/by-name/" customer-name)
        resp (http-client/req! http {:url url :method :get})]
    resp))

(defn customer->account [customer-id storage]
  (->> storage
       db.saving-account/accounts
       vals
       (filter #(= customer-id (:customer-id %)))
       first))

(defn delete-account! [account-id storage]
  (db.saving-account/remove-account! account-id storage))

(defn create-account! [customer-id storage http]
  (let [customer-url  (str "http://customer-service.com/customer/" customer-id)
        resp-body     (->> {:url customer-url :method :get}
                           (http-client/req! http)
                           :body)
        account       (logic/new-account customer-id
                                         (:customer-name resp-body))]
    (println customer-id)
    (println "ACCOUNT\t" account)
    (db.saving-account/add-account! account storage)
    (println (db.saving-account/accounts storage))
    account))

(defn get-account [account-id storage]
  (db.saving-account/account account-id storage))

