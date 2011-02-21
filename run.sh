javac Main.java
jar cvf CTF.jar Main\$BasicAudioPlayer.class Main.class Main\$GamePanel\$1.class Main\$GamePanel\$2.class Main\$GamePanel\$3.class Main\$GamePanel.class Main\$Resources.class Main\$RulesPanel.class Main\$RulesPanel\$1.class music.au requiem.au
rm Main\$BasicAudioPlayer.class Main.class Main\$GamePanel\$1.class Main\$GamePanel\$2.class Main\$GamePanel\$3.class Main\$GamePanel.class Main\$Resources.class Main\$RulesPanel.class Main\$RulesPanel\$1.class
chmod +x CTF.jar
padsp /etc/alternatives/java -classpath CTF.jar Main
