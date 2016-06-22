/**
 * 
 */

function getElementFromIFrame(iframeId, elementId){
	var ifr = getByIdOrName( iframeId );
	var ifrDoc = ifr.contentDocument || ifr.contentWindow.document;
	return ifrDoc.getElementById( elementId );
}

function getByIdOrName(idOrName){
	var element = document.getElementById( idOrName );
	
	if(element){
		return element;
	}
	
	element = document.getElementsByName( idOrName );
	
	if(element && element.length > 0){
		return element[0];
	}
	
	return null;
}