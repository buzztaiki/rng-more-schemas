;;; rng-more-schemas.el --- add dtd and xsd support to rng-schema.

;; Copyright (C) 2010  Taiki SUGAWARA

;; Author: Taiki SUGAWARA <buzz.taiki@gmail.com>
;; Keywords: xml

;; This program is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

;;; Commentary:

;; This package provides dtd and xsd support for rng-schema.
;; It convert dtd and xsd to rnc automaticaly using trang and rngconv.

;;; Usage:
;; Put the following to your .emacs:
;;
;;      (require 'rng-more-schemas)
;;      (setq rng-more-schemas-trang-jar "/path/to/trang.jar")
;;      (setq rng-more-schemas-rngconv-jar "/path/to/rngconv.jar")
;;      (rng-more-schemas-setup)
;;
;; Then you can use xsd and dtd in nXML's schema.xml:
;;
;;      <namespace ns="http://www.w3.org/1999/xhtml" uri="xhtml-strict.xsd"/>


;;; Code:

(require 'rng-loc)
(require 'rng-cmpct)

(defvar rng-more-schemas-trang-jar nil
  "Location of trang.jar.
Trang is Multi-format schema converter based on RELAX NG.

see: https://www.thaiopensource.com/relaxng/trang.html")

(defvar rng-more-schemas-rngconv-jar nil
  "Location of rngconv.jar.
Sun RELAX NG Converter is a tool to convert schemas written in various
schema languages to their equivalent in RELAX NG.

see: https://github.com/kohsuke/msv")

(defvar rng-more-schemas-java-command "java"
  "Location of java command.")

(defvar rng-more-schemas-schema-loader-alist
  '(("dtd" . rng-more-schemas-load-schema)
    ("xsd" . rng-more-schemas-load-schema)))

(defvar rng-more-schemas-convert-buffer-name "*rng-more-schemas convert*")

(defun rng-more-schemas-call-process (program buffer &rest args)
  (= (apply #'call-process program nil buffer nil args) 0))
  
(defun rng-more-schemas-trang (schema output)
  (rng-more-schemas-call-process rng-more-schemas-java-command
				 rng-more-schemas-convert-buffer-name
				 "-jar" rng-more-schemas-trang-jar
				 schema output))


(defun rng-more-schemas-rngconv (schema output)
  (rng-more-schemas-call-process rng-more-schemas-java-command
				 rng-more-schemas-convert-buffer-name
				 "-jar" rng-more-schemas-rngconv-jar
				 schema output))

(defun rng-more-schemas-need-convert-p (schema rnc)
  (and (file-exists-p schema)
       (or (not (file-exists-p rnc))
	   (time-less-p
	    (nth 5 (file-attributes rnc))
	    (nth 5 (file-attributes schema))))))

(defun rng-more-schemas-rnc-file (schema)
  (concat schema ".rnc"))

(defun rng-more-schemas-load-schema (schema)
  (let ((rnc (rng-more-schemas-rnc-file schema)))
    (when (rng-more-schemas-need-convert-p schema rnc)
      (rng-more-schemas-convert-schema schema rnc))
    (when (file-exists-p rnc)
      (rng-c-load-schema rnc))))

(defun rng-more-schemas-convert-schema (schema rnc)
  (if (string= (file-name-extension schema) "xsd")
      (let ((rng (make-temp-file "rng-more-schemas-" nil ".rng")))
	(and (rng-more-schemas-rngconv schema rng)
	     (prog1 (rng-more-schemas-trang rng rnc)
	       ;; (delete-file rng)
	       )))
    (rng-more-schemas-trang schema rnc)))

(defun rng-more-schemas-setup ()
  (setq rng-schema-loader-alist
	(append rng-more-schemas-schema-loader-alist
		rng-schema-loader-alist)))

(provide 'rng-more-schemas)
;;; rng-more-schemas.el ends here
