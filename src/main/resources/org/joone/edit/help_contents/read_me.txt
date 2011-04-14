Developer's Notes
-----------------

If anyone wants to add to the on line help, you need to create an html page,
preferably with the same sort of style as the existing pages. 
To add the page into the system, edit joone.jhm and add your page as a
mapID. Edit jooneTOC.xml and add your new mapID at the required point in the
menu structure. Finally, you need to update the search index. 
The help system consists of a set of help documents and a search index.

If the contents of the help documentation changes, the search index must be updated.

Update the search index as follows:

1. Install Java Help. This can be found at http://java.sun.com/products/javahelp/

2. Add <your_base_dir>\jh1.1\javahelp\bin to your path.

3. At a dos prompt, cd to ....\org\joone\edit\help_contents.

4. Type jhindexer introduction Tutorial Concepts reference_information Resources

This should update the files in the ....\org\joone\edit\help_contents\JavaHelpSearch directory with information for all the help directories.

Give pmarrone at users.sourceforge.net a call if you have any problems.
