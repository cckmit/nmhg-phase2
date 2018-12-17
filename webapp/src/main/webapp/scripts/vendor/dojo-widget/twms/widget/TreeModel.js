dojo.declare('twms.widget.TreeModel', dijit.tree.ForestStoreModel, {
        treeType: null,
        claimDetail: null,
        causalPart: null,
        selectedBusinessUnit : null,
        isAsync : true,
		getChildren: function(parentItem, complete_cb, error_cb) {
			if(parentItem.root){
                complete_cb(this.store.data.asmChildren);
            }
            else {
                var params = { claimDetail : this.claimDetail,
                               causalPart : this.causalPart,
                               selectedBusinessUnit : this.selectedBusinessUnit,
                               assemblyId: parentItem.id  };
                if(this.treeType === 'FAULTCODE'){
                    if(this.isAsync){
                        twms.ajax.fireJsonRequest("get_fault_code_json.action", params, function(data) {
                            complete_cb(data);
                        }, function onError(data){
                            console.error("Invalid tree type : '" + this.treeType + "'");
                        });                        
                    }else{
                        complete_cb(parentItem.asmChildren);
                    }
                }else if(this.treeType === 'JOBCODE'){
                    if(this.isAsync){
                        twms.ajax.fireJsonRequest("get_service_procedure_json.action", params, function(data) {
                            complete_cb(data);
                        }, function onError(data){
                            console.error("Invalid tree type : '" + this.treeType + "'");
                        });
                    }else{
                        var children = dojo.filter(parentItem.asmChildren, function(item){
                            return item.nodeType === 'node';
                        });
                        if(parentItem.spChildren && parentItem.spChildren.length > 0)
                            children = children.concat(parentItem.spChildren);
                        complete_cb(children);
                    }
                }else{
                    console.error("Invalid tree type : '" + this.treeType + "'");
                    complete_cb([]);
                }
            }
		},

		mayHaveChildren: function(item) {
			if(item.root)
				return true;
            else if(this.isAsync && (this.treeType === 'FAULTCODE' || this.treeType === 'JOBCODE')){
                return item.nodeType === 'node';
            }else if(this.treeType === 'FAULTCODE'){
                return item.asmChildren.length > 0;
            }else if(this.treeType === 'JOBCODE'){
                return ((item.spChildren && item.spChildren.length > 0) || 
                        dojo.some(item.asmChildren, function(itemNode){
                            return itemNode.nodeType === 'node';
                        }));
            }else{
                console.error("Invalid tree type : '" + this.treeType + "'");
            }
            return false;
		}
	});