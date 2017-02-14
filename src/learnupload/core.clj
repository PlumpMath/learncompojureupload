(ns learnupload.core
  (:use [ring.middleware.params]
        [ring.middleware.multipart-params])
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [hiccup.core :refer [html]])
  (:import [java.io File FileInputStream FileOutputStream])
  (:gen-class))

(def resource-path "/home/debtao/Desktop")

(defn home-page []
  (html
   [:form {:action "/upload" :method "post" :enctype "multipart/form-data"}
    [:input {:id "file" :name "file" :type "file" }]
    [:input {:type "submit" :name "submit" :value "submit"}]]))

(defn file-path [path & [filename]]
  (java.net.URLDecoder/decode
   (str path File/separator filename) "utf-8"))

(defn upload-file
  "uploads a file to the target folder
   when :create-path? flag is set to true then the target path will be created"
  [path {:keys [tempfile size filename]}]
  (try
    (with-open [in (new FileInputStream tempfile)
                out (new FileOutputStream (file-path path filename))]
      (let [source (.getChannel in)
            dest (.getChannel out)]
        (.transferFrom dest source 0 (.size source))
        (.flush out)))))

(defroutes handler
  (GET "/upload" [] (home-page))
  (wrap-multipart-params
   (POST "/upload" {params :params}
         (let [file (get params "file")]
           (upload-file resource-path file))
         )))

(defn -main
  "Run server"
  []
  (run-server handler {:port 5000}))
 
