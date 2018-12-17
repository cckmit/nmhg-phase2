            <script type="text/javascript">
        dojo.addOnLoad(function() {
            // restore original overflow value of body.
            bodyStyle.overflow = docBody._originalOverflow;
            docBody._originalOverflow = undefined;

            if(!__tabLidActive) {
                dojo.dom.destroyNode(dojo.byId("startupLid"));
            }
        });

        if(__tabLidActive) { //defined in body.ftl
            __iframe.__twmsPageLoaded = true;
            delete __iframe;
        }

        delete bodyStyle;
        delete docBody;
    </script>
</body>
