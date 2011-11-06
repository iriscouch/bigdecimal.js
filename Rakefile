require 'erb'

HERE = File.expand_path(File.dirname __FILE__)
GWT  = "#{HERE}/BigDecimalApp"
GWT_SRC = "#{GWT}/src/com/iriscouch/gwtapp/client"
CJS_BUILD = "#{HERE}/lib"
CJS_PATH = "#{CJS_BUILD}/bigdecimal.js"

java_sources = %w[ RoundingMode MathContext BigInteger BigDecimal BigDecimalApp ]
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

directory CJS_BUILD

file CJS_PATH => [CJS_BUILD] + java_sources.map{|x| "#{GWT_SRC}/#{x}.java"} do |task|
  # Build the base GWT library.
  Dir.chdir GWT do
    sh 'ant build' unless ENV['skip_ant']
  end

  gwt_js = Dir.glob("#{GWT}/war/gwtapp/#{'?' * 32}.cache.js").last
  puts "Using compiled JS: #{gwt_js}"
  gwt_source = File.new(gwt_js).read

  # Insert the code required to initialize the library. This is text manipulation to reach inside
  # a function closure. Better ideas welcome!
  loader = "gwtOnLoad(null, 'ModuleName', 'moduleBase');"
  gwt_source.gsub! /(\}\)\(\);)$/, "\n#{loader}\n\\1"

  js = ERB.new <<-EOT
    if(typeof document === 'undefined')
      var document = {};

    if(typeof window === 'undefined')
      var window = {};
    if(!window.document)
      window.document = document;

    if(typeof navigator === 'undefined')
      var navigator = {};
    if(!navigator.userAgent)
      navigator.userAgent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/534.51.22 (KHTML, like Gecko) Version/5.1.1 Safari/534.51.22';

    function gwtapp() {};

    <%= gwt_source %>

    exports.RoundingMode = window.bigdecimal.RoundingMode;
    exports.MathContext = window.bigdecimal.MathContext;

    // This is an unfortunate kludge because Java methods and constructors cannot accept vararg parameters.
    var fix_and_export = function(class_name) {
      var Src = window.bigdecimal[class_name];
      var Fixed = Src;
      if(Src.__init__) {
        Fixed = function wrap_constructor() {
          var args = Array.prototype.slice.call(arguments);
          return Src.__init__(args);
        };

        Fixed.prototype = Src.prototype;

        for (var a in Src)
          if(Src.hasOwnProperty(a)) {
            if((typeof Src[a] != 'function') || !a.match(/_va$/))
              Fixed[a] = Src[a];
            else {
              var pub_name = a.replace(/_va$/, '');
              Fixed[pub_name] = function wrap_classmeth () {
                var args = Array.prototype.slice.call(arguments);
                return wrap_classmeth.inner_method(args);
              };
              Fixed[pub_name].inner_method = Src[a];
            }
          }

      }

      var proto = Fixed.prototype;
      for (var a in proto) {
        if(proto.hasOwnProperty(a) && (typeof proto[a] == 'function') && a.match(/_va$/)) {
          var pub_name = a.replace(/_va$/, '');
          proto[pub_name] = function wrap_meth() {
            var args = Array.prototype.slice.call(arguments);
            return wrap_meth.inner_method.apply(this, [args]);
          };
          proto[pub_name].inner_method = proto[a];
          delete proto[a];
        }
      }

      exports[class_name] = Fixed;
    };

    fix_and_export('BigDecimal');
    fix_and_export('BigInteger');
  EOT

  File.new(task.name, 'w').write(js.result binding)
  puts "Generated #{File.basename task.name}"
end

desc 'Build CommonJS BigDecimal library'
task :bigdecimal => CJS_PATH

task :default => :bigdecimal

desc 'Clean up'
task :clean do
  sh "rm -rfv #{CJS_BUILD} #{GWT_SRC}/Big*.java #{GWT_SRC}/MathContext.java #{GWT_SRC}/RoundingMode.java"
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
 8. git reset --hard "$ver"^
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
