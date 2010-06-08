require 'erb'

HERE = File.expand_path(File.dirname __FILE__)
GWT  = "#{HERE}/GwtApp"
GWT_SRC = "#{GWT}/src/io/couch/gwtapp/client"

file "#{GWT_SRC}/BigDec.java" => "#{GWT_SRC}/BigDec.java.erb" do |t|
  erb_to_java t.prerequisites.first, t.name
end

desc 'Build GWT application'
task :gwt => "#{GWT_SRC}/BigDec.java" do
  Dir.chdir GWT do
    sh 'ant build'
  end
end

desc 'Build CommonJS library'
task :commonjs => :gwt do
  puts 'Hi!'
end

task :default => :commonjs

#
# Helper functions
#

def erb_to_java(source, dest)
  src = ERB.new(File.new(source).read)
  java = File.new(dest, 'w')
  java.write(src.result(binding))
  java.close
end
