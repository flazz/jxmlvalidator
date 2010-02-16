require 'semver'

Gem::Specification.new do |spec|
  spec.name = "jxmlvalidator"
  spec.version = SemVer.find.format '%M.%m.%p'
  spec.summary = "JAXP based XML Validation"
  spec.email = "flazzarino@gmail.com"
  spec.homepage = 'http://github.com/flazz/jxmlvalidator'
  spec.authors = ["Francesco Lazzarino"]
  
  spec.files = [
    "README.md",
    "ext/xmlvalidator.jar",
    "jxmlvalidator.gemspec", 
    "lib/jxml/validator.rb",
    "spec/jxml/validator_spec.rb"
  ]

  spec.add_dependency 'rjb', '~> 1.2.0'
  spec.requirements << 'a Java environment with JAXP'
end
