##### General info

This project builds a humongous word list (over 120k words if I'm not mistaken) and simply uses web interface of krdict.korean.go.kr to look each of them up. Not very clever approach, but at the time I didn't know they had an API (information about it is written only in Korean version of their site), so before I learned about it I had most of the work done. The website is parsed and a local file with an entry is created. When everything is done dictionary files in amazon required format are produced which are the input of kindlegen that actually creates an output mobi file. So first download [kindlegen from amazon](https://www.amazon.com/gp/feature.html?ie=UTF8&docId=1000765211).

This project can produce only one jar which is used to look up words. Other actions are simply executed main methods.

##### Preparation
- first download kindlegen from amazon.
- build inflections file executing org.mchs.dict.local.InputDictionaryWordListsOperations.main. It will also create words_to_process.txt and intermediary file base_nouns_possessives.txt, yet the 2 of them will not be needed for an easy path. And base_nouns_possessives.txt required lifespan ended when combined_inflections.txt was created
- now choose your path

##### The hard way

It's for you if you have plenty of time.

- if you really want to start from scratch delete _dict.entries.in_ content
- run mvn clean install to build one jar that will query krdict.korean.go.kr dictionary, this jar takes one word as an argument
- a list of words _words_to_process.txt_ (created in preparation stage) is really long and quering takes time so you'd better find something to do for the next couple of days. Even if I had known and used their API they allow only 25,000 queries a day, so at best it would have taken 5 days. Unless you rewrite this code using threads and use some clever tricks to change your IP you're in for a long wait
- look up words - you may use perl script callKrDict.pl from scripts directory (this requires perl installed). A good idea is to call it from within dict.entries.in directory as it saves an outout file in the directory you're in which will save you time later on. It will omit words that were already looked up, also you can pass a letter or a string as an argument to look up only word beginning with that argument
- it's rare, but you may not get an actual exception but just a couple of html tags without real content. I'm not sure if the problem is in my code or the dictionary service returns something what I didn't account for. This is not easily reproducible (I got it a couple of dozens of times for over 120,000 queries and I wasn't very inclined to check it). If you'll use perl script you'll notice that if word wasn't found output file will be like 40 bytes long and slightly bigger (46 I think) if  parsing went south. Simply remove offending files and rerun the script
- jar generates one file per entry. When yu're done with generating them put them in _dict.entries.in_ directory
- all hard work is now done, so continue the easy way

##### The easy way
- all entries available in May 2018 are already in dict.entries.in, and we already have inflections file so what remains is to just build files required by kindlegen and an output mobi file.
- run org.mchs.dict.local.KindleRequiredFilesCreator.main which will create dict.out directory and all files needed by kindlegen.exe inside
- if you want to modify them make sure you've read [_AmazonKindlePublishingGuidelines_](http://kindlegen.s3.amazonaws.com/AmazonKindlePublishingGuidelines.pdf) first
- open console and run kindlegen, e.g. if you're on windows

```kindlegen.exe EnglishKoreanLearnersDictionary.opf -c2 -verbose ```

or

`kindlegen.exe EnglishKoreanLearnersDictionary.opf -c2 -verbose -dont_append_source`
- go get yourself a cup coffee, do the laundry, walk the dog, etc.
- enjoy your new shiny mobi file which can be transferred to kindle using usb or sent to your kindle address

##### Update only

 File _not_found_files_ contains a list of words that weren't found in krdict.korean.go.kr at the time of writing this. So I'm planning on checking if anything's changed every now and then and maybe update the dictionary.

##### Additional information

_dict.entries.in_ directory contains htmlized entry files that consist of "header" and "main entry". Header is the very first line of the file and contains all the Korean translations grouped by part of speech. Main entry gives more of a context in which specific words can be used. If you're happy with only translations and you don't care about the context you may use org.mchs.dict.local.StarDictFileCreator class to generate simple text file with lines built like:

word\<TAB\>translations

but you have to find tools like tab2opf on the internet e.g. [1manfactory.com/create-your-own-kindle-dictionary-for-every-language-for-free/](1manfactory.com/create-your-own-kindle-dictionary-for-every-language-for-free/) and build the dictionary using their instructions.