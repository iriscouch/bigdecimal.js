require 'erb'

HERE = File.expand_path(File.dirname __FILE__)
GWT  = "#{HERE}/GwtApp"
GWT_SRC = "#{GWT}/src/io/couch/gwtapp/client"
CJS_PATH = "#{HERE}/build/bigdecimal.js"

%w[ BigDec ].each do |class_name|
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

file CJS_PATH => "#{GWT_SRC}/BigDec.java" do |task|
  # Build the base GWT library.
  Dir.chdir GWT do
    sh 'ant build'
  end

  gwt_js = Dir.glob("#{GWT}/war/gwtapp/#{'?' * 32}.cache.js").last
  sh "cp #{gwt_js} #{task.name}"
end

desc 'Build CommonJS library'
task :commonjs => CJS_PATH

task :default => :commonjs
