require 'erb'

HERE = File.expand_path(File.dirname __FILE__)
GWT  = "#{HERE}/BigDecimalApp"
GWT_SRC = "#{GWT}/src/com/iriscouch/gwtapp/client"
CJS_DIR = "#{HERE}/lib"
JS_BUILD = "#{HERE}/build/out.js"
CJS_PATH = "#{CJS_DIR}/bigdecimal.js"

task :default => :bigdecimal

directory CJS_DIR

java_sources = %w[ RoundingMode MathContext BigInteger BigDecimal BigDecimalApp ]
java_sources.each do |class_name|
  java_source_path = "#{GWT_SRC}/#{class_name}.java"

  file JS_BUILD => java_source_path
  file java_source_path => "#{java_source_path}.erb" do |task|
    erb_path = task.prerequisites.first
    java_path = task.name

    src = ERB.new(File.new(erb_path).read)
    java = File.new(java_path, 'w')
    java.write(src.result(binding))
    java.close

    puts "#{class_name}.java.erb => #{class_name}.java"
  end
end

file JS_BUILD => CJS_DIR do |task|
  # Build the base GWT library.
  Dir.chdir GWT do
    sh 'ant build' unless ENV['skip_ant']
  end

  gwt_js = Dir.glob("#{GWT}/war/gwtapp/#{'?' * 32}.cache.js").last
  puts "Using compiled JS: #{gwt_js}"
  gwt_source = File.new(gwt_js).read

  File.new(task.name, 'w').write(gwt_source)
  puts "#{gwt_js} => #{task.name}"
end

file CJS_PATH => [JS_BUILD, "commonjs_wrapper.js.erb"] do |task|
  gwt_source = File.new(JS_BUILD).read

  # Insert the code required to initialize the library. This is text manipulation to reach inside
  # a function closure. It would be nice to switch to UglifyJS.
  loader = "gwtOnLoad(null, 'ModuleName', 'moduleBase');"
  gwt_source.gsub! /(\}\)\(\);)$/, "\n#{loader}\n\\1"

  wrapper_src = File.new("commonjs_wrapper.js.erb").read
  js = ERB.new wrapper_src

  File.new(task.name, 'w').write(js.result binding)
  puts "Generated #{File.basename task.name}"
end

desc 'Build CommonJS BigDecimal library'
task :bigdecimal => CJS_PATH

desc 'Push a demo BigDecimal Couch app'
task :couchapp => [CJS_PATH, "#{HERE}/CouchDB/demo.js"] do
  couchapp = "#{HERE}/node_modules/.bin/couchapp"
  raise "Can't find command #{couchapp}, did you 'npm install --dev'?" unless File.exist?(couchapp)

  raise "Please specify a url parameter, e.g. url=http://admin:secret@example.iriscouch.com/demo" unless ENV['url']

  sh couchapp, "push", "#{HERE}/CouchDB/demo.js", ENV['url']

  url = ENV['url']
  url = url.sub /\/+$/, ""
  url = url.sub /^(https?):\/\/.*?:.*?@(.*)$/, "\\1://\\2"

  puts ""
  puts "Demo URL: #{url}/_design/bigdecimal/_show/ui"
end

desc 'Clean up'
task :clean do
  sh "rm -rfv #{CJS_DIR} #{JS_BUILD} #{GWT_SRC}/Big*.java #{GWT_SRC}/MathContext.java #{GWT_SRC}/RoundingMode.java"
end

desc 'Show how to tag a revision'
task :tag do
  puts <<EOT
How to Tag a Release
====================

I do not like generated code being managed by Git. However that is useful
when people download tarballs from GitHub, etc. So the idea is to have a
revision "spur" off the development line which only generates the code
and commits the tag.

 1. Confirm the repo is clean
 2. rake clean && rake
 3. git add -f #{CJS_PATH} && git commit -m "Code release"
 4. ver="vX.Y.Z" # Set this to something.
 5. git tag -a -m "Tag release" "$ver"
 6. git push origin "$ver:/refs/tags/$ver"
 7. npm publish
 8. Edit package.json and bump the version
 9. git rm #{CJS_PATH}
 10. git commit -m 'Working on <new version>'
EOT
end

#
# Helpers
#

def wrap(return_type, name, *signatures)
  # When there is no ambiguity (i.e. only one method signature) just wrap it directly.
  return wrap_nosigs(return_type, name, *signatures) if signatures.length <= 1

  # Use the call_signatures system to call the correct Java method and return it back to JS.
  lines = []
  lines << "public #{return_type} #{name}_va(JsArgs args) {"
  lines << "#{return_type} result;"

  lines << "// return_type.to_s[0..2] = #{return_type.to_s[0..2]}"
  if return_type.to_s[0..2] != 'Big'
    call = "result = super.#{name}"
  else
    lines << "java.math.#{return_type} interim;"
    call = "interim = super.#{name}"
  end
  lines << call_signatures('args', call, *signatures)
  lines << "result = new #{return_type}(interim);" if return_type.to_s[0..2] == 'Big'
  lines << "return result;"
  lines << "}"
  return lines.join("\n")
end

def wrap_nosigs(return_type, name, *param_types)
  formal = []; actual = []
  param_types.each_with_index do |param_type, a|
    formal.push "#{param_type} var#{a}"
    actual.push %w[ MathContext RoundingMode ].include?(param_type.to_s) ? "new java.math.#{param_type}(var#{a}.toString())" : "var#{a}"
  end

  call = "super.#{name}(#{actual.join ', '})"
  expr = call
  #expr = "new #{return_type}(#{call})" if return_type.to_s[0..2] == 'Big'
  expr = "new #{return_type}(#{call})" if %w[ BigInteger BigDecimal MathContext RoundingMode ].include?(return_type.to_s)

  "public #{return_type} #{name}(#{formal.join ', '}) { return #{expr}; }"
end

def call_signatures(args, expression, *signatures)
  lines = []
  lines << "String sig = JsArgs.signature(#{args});"
  signatures.each_with_index do |sig, a|
    js_types = []
    param_types = []
    sig.to_s.split.each do |param, a|
      if %w[ int double ].include? param
        js_types << 'number'
        param_types << param.capitalize
      elsif param == 'string'
        js_types << 'string'
        param_types << 'String'
      elsif param == 'char_array'
        js_types << 'array'
        param_types << 'CharArray'
      else
        js_types << param
        param_types << param
      end
    end
    lines << "#{a == 0 ? 'if' : 'else if'}(sig == \"#{js_types.join ' '}\")"
    actuals = []
    param_types.each_with_index do |param_type, b|
      actuals << "#{args}.get#{param_type}(#{b})"
    end
    lines << "  #{expression}(#{actuals.join ', '});"
  end

  lines << "else throw new RuntimeException(\"Unknown call signature for #{expression}: \" + sig);"
  return lines.join("\n");
end
