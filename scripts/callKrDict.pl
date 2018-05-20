#!env perl

## executing this script with an argument will limit list of words
## to these starting with this argument
## e.g. running ./callKrDict.pl p
## will make it use only words starting with a 'p'
## similarly running ./callKrDict.pl irr
## will omit all words except these that start with 'irr'

$WORDLIST = "../src/main/resources/words_to_process.txt";
open(WORDLIST) or die("Could not open word file.");
foreach $line (<WORDLIST>) {
    chomp($line);

    if ( $line =~ m/'/ ){
	    next;
    }
    if ( $line !~ m/^$ARGV[0]/ ){
	    next;
    }

    if ( -f $line ){
        print "File $line exists\n";
    } else {
        print "File $line doesn't exist\n";
        $output = `java -Dfile.encoding=UTF-8  -jar ../target/krdict-reader-1.0-SNAPSHOT.jar  $line 2>&1`;

        if ( $output =~ m/No search results starting the key word/) {
            system("echo 'No search results starting the key word' > $line");
        }

    }
}
close(WORDLIST);

__END__
