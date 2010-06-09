require 'erb'

HERE = File.expand_path(File.dirname __FILE__)
GWT  = "#{HERE}/GwtApp"
GWT_SRC = "#{GWT}/src/io/couch/gwtapp/client"
CJS_PATH = "#{HERE}/build/bigdecimal.js"

java_sources = %w[ BigDec RoundingMode MathContext ]
java_sources.each do |class_name|
  file "#{GWT_SRC}/#{class_name}.java" => "#{GWT_SRC}/#{class_name}.java.erb" do |task|
    erb_path = task.prerequisites.first
    java_path = task.name

    src = ERB.new(File.new(erb_path).read)
    java = File.new(java_path, 'w')
    java.write(src.result(binding))
    java.close

    puts "#{class_name}.java.erb => #{class_name}.java"
  end
end

file CJS_PATH => java_sources.map{|x| "#{GWT_SRC}/#{x}.java"} do |task|
  # Build the base GWT library.
  Dir.chdir GWT do
    sh 'ant build'
  end

  gwt_js = Dir.glob("#{GWT}/war/gwtapp/#{'?' * 32}.cache.js").last
  puts "Using compiled JS: #{gwt_js}"
  gwt_source = File.new(gwt_js).read

  # Insert the code required to initialize the library. This is text manipulation to reach inside
  # a function closure. Better ideas welcome!
  loader = "gwtOnLoad(null, 'ModuleName', 'moduleBase');"
  gwt_source.gsub! /(}\)\(\);)$/, "\n#{loader}\n\\1"

  js = ERB.new <<-EOT
    document = {};
    window = { "document": document };
    function gwtapp() {};
    <%= gwt_source %>
    exports.BigDec = window.j.BigDec;
    exports.RoundingMode = window.j.RoundingMode;
    exports.MathContext = window.j.MathContext;
  EOT

  File.new(task.name, 'w').write(js.result binding)
  puts "Generated #{File.basename task.name}"
end

desc 'Build CommonJS BigDecimal library'
task :bigdecimal => CJS_PATH

task :default => :bigdecimal

#
# Helpers
#

def wrap(return_type, name, *param_types)
  formal = []; actual = []
  param_types.each_with_index do |param_type, a|
    formal.push "#{param_type} var#{a}"
    actual.push "var#{a}"
  end

  call = "super.#{name}(#{actual.join ', '})"
  expr = call
  expr = "new #{return_type}(#{call})" if return_type.to_s[0..2] == 'Big'

  "public #{return_type} #{name}(#{formal.join ', '}) { return #{expr}; }"
end
