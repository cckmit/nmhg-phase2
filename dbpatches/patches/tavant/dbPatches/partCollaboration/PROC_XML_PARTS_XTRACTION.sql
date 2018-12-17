--Purpose    : Procedure to parse the Part Inventory XML and insertion into Staging Table
--Author     : Kaushal Soni & Priyank Gupta
--Created On : 07-Sep-2008
--Created By : Jhulfikar Ali A

create or replace
PROCEDURE xml_parts_xtraction (
   p_documentid         NUMBER,
   loggedinuser   OUT   NUMBER
)
AS
   CURSOR blob_file
   IS
      SELECT content
        --THIS FIELD VALUE WILL CHANGE BELOW IF CURSOR IS MODIFIED
      FROM   parts_upload
       WHERE document_type = 'PARTS_INVENTORY_XML' AND ID = p_documentid;

   current_clob_value               CLOB;
   v_inv_part_count                 NUMBER;
   v_part_inv_element_count         NUMBER;
   v_create_date                    VARCHAR2 (20);
   v_login                          VARCHAR2 (255);
   v_system_name                    VARCHAR2 (255);
   v_xml_part_count                 NUMBER;
   v_dealer_number                  VARCHAR2 (100);
   v_login_element_count            NUMBER;
   v_xml_prt_qty_val                VARCHAR2 (20);
   v_xml_prt_number_val             VARCHAR2 (100);
   v_current_element_name           VARCHAR2 (100);
   v_error                          VARCHAR2 (4000)    := NULL;
   v_namespace_type                 VARCHAR2 (100)
                                              := ' xsi:type="soapenc:string"';
   v_namespace_xmlns                VARCHAR2 (100)
              := ' xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"';
   v_namespace                      VARCHAR2 (100)
                            := '{http://parts.layer.integration.twms.tavant}';
   v_namespace_default              VARCHAR2 (200)
                     := ' xmlns="http://parts.layer.integration.twms.tavant"';
   v_header                         VARCHAR2 (100)
                               := '???<?xml version="1.0" encoding="utf-8"?>';
   v_header2                        VARCHAR2 (100) := '<?xml version="1.0"?>';
   v_extra_namespace                VARCHAR2 (200)
      := ' xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema"';
   v_incoming_xml_doc               xmldom.domdocument;
   v_incoming_parser                xmlparser.parser;
   v_num_of_part_inv_sets           xmldom.domnodelist;
   v_num_of_part_login_detail       xmldom.domnodelist;
   v_each_part_child_node_list      xmldom.domnodelist;
   v_login_detail_child_node_list   xmldom.domnodelist;
   v_xml_part_list                  xmldom.domnodelist;
   v_xml_each_part_element          xmldom.domelement;
   v_each_part_inv_node             xmldom.domnode;
   v_login_detail_node              xmldom.domnode;
   v_xml_part_inv_element_node      xmldom.domnode;
   v_login_element_node             xmldom.domnode;
   v_xml_each_part_node             xmldom.domnode;
BEGIN
   FOR each_blob_file IN blob_file
   LOOP
      current_clob_value :=
                   xml_part_xtraction_util.blob2clob (each_blob_file.content);

      SELECT REPLACE (current_clob_value, v_namespace, '')
        INTO current_clob_value
        FROM DUAL;

      SELECT REPLACE (current_clob_value, v_namespace_type, '')
        INTO current_clob_value
        FROM DUAL;

      SELECT REPLACE (current_clob_value, v_namespace_xmlns, '')
        INTO current_clob_value
        FROM DUAL;

      SELECT REPLACE (current_clob_value, '&lt;', '<')
        INTO current_clob_value
        FROM DUAL;

      SELECT REPLACE (current_clob_value, '&gt;', '>')
        INTO current_clob_value
        FROM DUAL;

      SELECT REPLACE (current_clob_value, v_namespace_default, '')
        INTO current_clob_value
        FROM DUAL;

      SELECT REPLACE (current_clob_value, v_header, '')
        INTO current_clob_value
        FROM DUAL;

      v_incoming_parser := xmlparser.newparser;
      xmlparser.parseclob (v_incoming_parser, current_clob_value);
      v_incoming_xml_doc := xmlparser.getdocument (v_incoming_parser);
      v_num_of_part_login_detail :=
           xmldom.getelementsbytagname (v_incoming_xml_doc, 'ApplicationArea');
      v_login_detail_node := xmldom.item (v_num_of_part_login_detail, 0);
      v_login_detail_child_node_list :=
                                    xmldom.getchildnodes (v_login_detail_node);

      FOR v_login_element_count IN
         0 .. xmldom.getlength (v_login_detail_child_node_list) - 1
      LOOP
         BEGIN
            v_login_element_node :=
               xmldom.item (v_login_detail_child_node_list,
                            v_login_element_count
                           );
            v_current_element_name :=
                                     xmldom.getnodename (v_login_element_node);

            IF v_current_element_name = 'Sender'
            THEN
               v_login_detail_child_node_list :=
                                  xmldom.getchildnodes (v_login_element_node);
               v_login :=
                  xml_part_xtraction_util.get_child_node_value_by_tag
                                                        (v_login_detail_node,
                                                         'Login'
                                                        );
               v_system_name :=
                  xml_part_xtraction_util.get_child_node_value_by_tag
                                                         (v_login_detail_node,
                                                          'SystemName'
                                                         );
            END IF;
         END;
      END LOOP;

      v_num_of_part_inv_sets :=
            xmldom.getelementsbytagname (v_incoming_xml_doc, 'InventoryGroup');

      FOR v_inv_part_count IN 0 ..   xmldom.getlength (v_num_of_part_inv_sets)
                                   - 1
      LOOP
         BEGIN
            v_each_part_inv_node :=
                       xmldom.item (v_num_of_part_inv_sets, v_inv_part_count);
            v_each_part_child_node_list :=
                                  xmldom.getchildnodes (v_each_part_inv_node);
            v_dealer_number :=
               UPPER
                  (xml_part_xtraction_util.get_child_node_value_by_tag
                                                        (v_each_part_inv_node,
                                                         'DealerCode'
                                                        )
                  );

            FOR v_part_inv_element_count IN
               0 .. xmldom.getlength (v_each_part_child_node_list) - 1
            LOOP
               BEGIN
                  v_xml_part_inv_element_node :=
                     xmldom.item (v_each_part_child_node_list,
                                  v_part_inv_element_count
                                 );
                  v_current_element_name :=
                              xmldom.getnodename (v_xml_part_inv_element_node);

                  IF v_current_element_name = 'PartsInvItems'
                  THEN
                     v_xml_each_part_element :=
                             xmldom.makeelement (v_xml_part_inv_element_node);
                     v_xml_part_list :=
                        xmldom.getelementsbytagname (v_xml_each_part_element,
                                                     'InvItem'
                                                    );

                     FOR v_xml_part_count IN
                        0 .. xmldom.getlength (v_xml_part_list) - 1
                     LOOP
                        v_xml_each_part_node :=
                              xmldom.item (v_xml_part_list, v_xml_part_count);
                        v_xml_prt_number_val :=
                           xml_part_xtraction_util.get_child_node_value_by_tag
                                                       (v_xml_each_part_node,
                                                        'PartNo'
                                                       );
                        v_xml_prt_qty_val :=
                           xml_part_xtraction_util.get_child_node_value_by_tag
                                                        (v_xml_each_part_node,
                                                         'Qty'
                                                        );

                        INSERT INTO stg_parts_inventory
                                    (ID,
                                     dealer_no, part_number,
                                     quantity,
                                     document_type, login, create_date
                                    )
                             VALUES (stg_parts_inventory_seq.NEXTVAL,
                                     v_dealer_number, v_xml_prt_number_val,
                                     v_xml_prt_qty_val,
                                     'PARTS_INVENTORY_XML', v_login, SYSDATE
                                    );
                     END LOOP;

                     COMMIT;
                  END IF;
               END;
            END LOOP;
         END;
      END LOOP;

      COMMIT;
   END LOOP;

   BEGIN
      SELECT ID
        INTO loggedinuser
        FROM org_user
       WHERE login = v_login;
   EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
         v_error := SUBSTR (SQLERRM, 1, 900);
      WHEN OTHERS
      THEN
         v_error := SUBSTR (SQLERRM, 1, 900);
   END;
END;
/