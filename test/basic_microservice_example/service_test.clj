(ns basic-microservice-example.service-test
  (:require [midje.sweet :refer :all]
            [basic-microservice-example.components :as components]
            [basic-microservice-example.http-helpers :refer [GET POST]]
            [basic-microservice-example.service :as service]))

(components/ensure-system-up! :test-system)

#_(fact "hitting account creation endpoint"
  (GET "/" 200) => 0)
#_(fact "hitting account creation endpoint"
  (GET "/baz/bar" 200) => 0)
#_(fact "hitting account creation endpoint"
  (GET "/from-customer/account/" 200) => 0)
#_(fact "we can get user links"
  (POST "/customer/100" 200) => 0)
#_(fact "hitting account creation endpoint"
  (POST "/account/" {:customer-id 2} 200) => 0)

(comment 
;(def service
  ;(::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest home-page-test
  (is (=
       (:body (response-for service :get "/"))
       "Hello World!"))
  (is (=
       (:headers (response-for service :get "/"))
       {"Content-Type" "text/html;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"
        "X-Download-Options" "noopen"
        "X-Permitted-Cross-Domain-Policies" "none"
        "Content-Security-Policy" "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;"})))

(deftest about-page-test
  (is (.contains
       (:body (response-for service :get "/about"))
       "Clojure 1.8"))
  (is (=
       (:headers (response-for service :get "/about"))
       {"Content-Type" "text/html;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"
        "X-Download-Options" "noopen"
        "X-Permitted-Cross-Domain-Policies" "none"
        "Content-Security-Policy" "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;"})))

)
