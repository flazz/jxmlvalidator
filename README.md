Java based XML Validator (for ruby)
===================================

Why do this when libxml & friends already support validation?

Libxml (fast) does not support mixed-namespace validation and as of this writing have no plans to. 
JAXP (slow) is a very complete XML stack and does support mixed-namespace validation.

quickstart
----------

    require 'jxml/validator'
    val = JXML::Validator.new
    results = val.validate some_big_nasty_xml
    
    results[:errors].each do |e|
      puts "#{e[:line]}: #{e[:message]}"
    end


TODO
----

- cache feature (already implemented in java, need ruby to hook into it)
- xml parsing options
- command line tool
- make gem
