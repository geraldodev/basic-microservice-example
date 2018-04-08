(ns the-next-big-server-side-thing.controller
  (:require [the-next-big-server-side-thing.protocols.http-client :as http-client]))

(defn get-customer! [customer-name http]
  (let [url  (str "http://customer-service.com/by-name/" customer-name)
        resp (http-client/req! http {:url url :method :get})]
    resp))

