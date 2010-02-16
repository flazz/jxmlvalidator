require 'jxml/validator'

describe JXML::Validator do

  subject { JXML::Validator.new }

  it "should return entries for warnings" do
    pending "i don't know how to get a warning"
  end

  it "should return entries for errors" do

    xml=<<-XML
    <mods xmlns="http://www.loc.gov/mods/v3"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd">
        <titleInfo>
          <TITLE>
            I'm a limousine ridin', jet flyin', kiss stealin', wheelin' dealin' son of a gun. WOOOO!!
          </TITLE>
        </titleInfo>
    </mods>
    XML

    results = subject.validate xml
    results[:fatals].should be_empty
    results[:errors].should have_exactly(1).items
    results[:warnings].should be_empty
  end

  it "should return entries for fatals" do

    xml=<<-XML
    <mods xmlns="http://www.loc.gov/mods/v3"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd">
        <titleInfo>
          <title>
            I'm a limousine ridin', jet flyin', kiss stealin', wheelin' dealin' son of a gun. WOOOO!!
          </TITLE>
        </titleInfo>
    </mods>
    XML

    results = subject.validate xml
    results[:fatals].should have_exactly(1).items
    results[:errors].should be_empty
    results[:warnings].should be_empty

  end

  it "should return an empty result set if nothing to report" do

    xml=<<-XML
    <mods xmlns="http://www.loc.gov/mods/v3"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd">
        <titleInfo>
          <title>
            I'm a limousine ridin', jet flyin', kiss stealin', wheelin' dealin' son of a gun. WOOOO!!
          </title>
        </titleInfo>
    </mods>
    XML

    results = subject.validate xml
    results[:fatals].should be_empty
    results[:errors].should be_empty
    results[:warnings].should be_empty
  end

end
