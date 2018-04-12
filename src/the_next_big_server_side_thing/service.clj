(ns the-next-big-server-side-thing.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [the-next-big-server-side-thing.controller :as controller]
            [the-next-big-server-side-thing.interceptors.error-info :as error-info]
            [ring.util.response :as ring-resp]))

(defn home-page
  [request]
  (ring-resp/response "Hello World!"))

(defn baz
  [request]
  ;(throw (ex-info "" {}))
  (ring-resp/response {:foo :bar}))

(defn customer->account
  [{{:keys [customer-id]} :path-params
    {:keys [storage]} :components}]
  (let [foo (controller/customer->account (read-string customer-id) storage)]
    (println foo)
    (ring-resp/response foo)))

(defn get-account
  [{{:keys [account-id]} :path-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (controller/get-account account-id storage)))

(defn delete-account
  [{{:keys [account-id]} :path-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (controller/delete-account! account-id storage)))

(defn create-account
  [{{:keys [customer-id]} :edn-params
    {:keys [http storage]} :components}]
  (let [account (controller/create-account! customer-id storage http)]
    (ring-resp/response account)))

(def common-interceptors
  [(body-params/body-params) http/html-body error-info/log-error-during-debugging])

(def routes
  #{["/" :get (conj common-interceptors `home-page)]
    ["/baz/bar" :get (conj common-interceptors `baz)]
    ["/account/" :post (conj common-interceptors `create-account)]
    ["/from-customer/account/:customer-id/" :get (conj common-interceptors `customer->account)]
    ["/account/:account-id/" :get (conj common-interceptors `get-account)]
    ["/account/remove/:account-id/" :post (conj common-interceptors `delete-account)]
    })
