
package help;

import java.net.URL;

class BookInfo {
        public String bookName;
        public URL bookURL;

        public BookInfo(String book, String filename) {
            bookName = book;
            bookURL = TestClientGUI.class.getResource(filename);
            if (bookURL == null) {
                System.err.println("Couldn't find file: "
                                   + filename);
            }
        }

        public String toString() {
            return bookName;
        }
    }
