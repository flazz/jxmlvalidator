require 'tempfile'
require 'rjb'

module JXML

  class Validator

    JAR_FILE = File.join File.dirname(__FILE__), '..', '..', 'ext', 'xmlvalidator.jar'

    def Validator.load_validator_class
      j_URI = Rjb.import 'java.net.URL'
      uri = j_URI.new "file://#{File.expand_path JAR_FILE}"

      j_URLClassLoader = Rjb.import 'java.net.URLClassLoader'
      loader = j_URLClassLoader.new [uri]

      j_Class = Rjb.import 'java.lang.Class'
      j_Class.forName "edu.fcla.da.xml.Validator", true, loader
    end

    J_File = Rjb.import 'java.io.File'
    J_Validator = load_validator_class

    def initialize
      @jvalidator = J_Validator.new
    end

    # return a result set hash with the keys
    # :fatals, :errors, :warnings that maps to an array
    # each containing the keys :line, :message, :column
    def validate src

      tio = Tempfile.open 'jxmlvalidator'
      tio.write src.to_s
      tio.flush
      tio.close
      jfile = J_File.new tio.path
      jchecker = @jvalidator.validate jfile
      tio.unlink

      results = {
        :fatals => j2r(jchecker.getFatals),
        :errors => j2r(jchecker.getErrors),
        :warnings => j2r(jchecker.getWarnings)
      }

    end

    private

    def j2r ja

      (0...ja.size).map do |n|
        e = ja.elementAt n

        {
          :line => e.getLineNumber,
          :message => e.getMessage,
          :column => e.getColumnNumber
        }

      end

    end

  end

end
