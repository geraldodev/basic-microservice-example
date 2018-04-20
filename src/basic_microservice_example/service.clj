(ns basic-microservice-example.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [basic-microservice-example.adapters :as adapters]
            [basic-microservice-example.controller :as controller]
            [basic-microservice-example.interceptors.error-info :as error-info]
            [ring.util.response :as ring-resp]))

(defn home-page
  [request]
  (ring-resp/response {:message "Hello World!"}))

(defn create-account
  [{{:keys [customer-id]} :edn-params
    {:keys [http storage]} :components}]
  (let [account (controller/create-account! customer-id storage http)]
    (ring-resp/response {:account account})))

(defn customer->account
  [{{:keys [customer-id]} :path-params
    {:keys [storage]} :components}]
  (let [account (controller/customer->account (adapters/str->uuid customer-id) storage)]
    (if account
      (ring-resp/response {:account account})
      (ring-resp/status
        (ring-resp/response {})
        400))))

(defn get-account
  [{{:keys [account-id]} :path-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (controller/get-account account-id storage)))

(defn delete-account
  [{{:keys [account-id]} :path-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (controller/delete-account! (adapters/str->uuid account-id) storage)))

(def common-interceptors
  [(body-params/body-params)
   http/html-body
   error-info/log-error-during-debugging])

(def routes
  #{["/" :get (conj common-interceptors `home-page)]
    ["/account/" :post (conj common-interceptors `create-account)]
    ["/account/from-customer/:customer-id/" :get (conj common-interceptors `customer->account)]
    ["/account/lookup/:account-id/" :get (conj common-interceptors `get-account)]
    ["/account/remove/:account-id/" :post (conj common-interceptors `delete-account)]})
