(ns image-grid.demo
  (:require [mikera.image.core :as mk]
            [image-grid.core :as ig]
            [image-grid.color :as color]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Examples using the image-grid/pixel functions. ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Create a new image with a reddish pixel and a short green line.
(let [img (-> (ig/new-image-grid 50 30)
              (ig/set-pixel [10 5] [0.8 0.0 1.0])
              (ig/set-pixel [25 20] [0.0 0.9 0.1])
              (ig/set-pixel [26 20] [0.0 0.9 0.1])
              (ig/set-pixel [27 20] [0.0 0.9 0.1])
              (ig/set-pixel [28 20] [0.0 0.9 0.1])
              (ig/set-pixel [29 20] [0.0 0.9 0.1])
              (ig/image-grid->mkimage))]
  (mk/save img "images/test.png") ;; save image as test.png 
  (mk/show img))           ;; show the image


;; Make an image filled with a gradient using map-image-grid
(mk/show
 (let [width 500
       height 300
       empty-image (ig/new-image-grid width height)
       pixel-fn (fn [[x y] _]
                  [(/ y height) (/ x width) 0.3])
       img-grid (ig/map-image-grid pixel-fn empty-image)]
   (ig/image-grid->mkimage img-grid)))


;; Load an image and make it redder
(mk/show
 (ig/image-grid->mkimage
  (ig/map-image-grid
   (fn [_ [r g b]]
     [(+ r 0.2) g b])
   (ig/mkimage->image-grid (mk/load-image "images/dog.jpg")))))


;; Load an image and use HSV encoding to change its hues
(mk/show
 (ig/image-grid->mkimage
  (ig/map-image-grid
   (fn [_ rgb]
     (let [[h s v] (color/rgb->hsv rgb)
           new-h (mod (+ h 0.3) 1)
           rgb (color/hsv->rgb [new-h s v])]
       rgb))
   (ig/mkimage->image-grid (mk/load-image "images/dog.jpg")))))
