package com.stackroute.activitystream.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.activitystream.dao.MessageDAO;
import com.stackroute.activitystream.model.Message;
import com.stackroute.activitystream.model.UserTag;

@RestController
@RequestMapping(value = "/api/message")
public class MessageController {

	@Autowired
	private MessageDAO messageDAO;

	@Autowired
	private UserTag userTag;

	// ---------------------Send Message to Circle---------------------------------
	@PostMapping("/sendMessageToCircle/{circleName}")
	public ResponseEntity<Message> sendMessageToCircle(@PathVariable("circleName") String circleName,
			@RequestBody Message message,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<Message>(HttpStatus.UNAUTHORIZED);
		}
		message.setSenderName(loggedInUserName);
		Boolean sendStatus = messageDAO.sendMessageToCircle(circleName, message);
		if (sendStatus) {
			return new ResponseEntity<Message>(HttpStatus.OK);
		} else {
			return new ResponseEntity<Message>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// -------------------Send Message to Users-------------------------------------
	@PostMapping("/sendMessageToUser/{receiverId}")
	public ResponseEntity<Message> sendMessageToUser(@PathVariable("receiverId") String receiverId,@RequestBody Message message,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<Message>(HttpStatus.UNAUTHORIZED);
		}
		message.setSenderName(loggedInUserName);
		Boolean sendStatus = messageDAO.sendMessageToUser(receiverId, message);
		if (sendStatus) {
			return new ResponseEntity<Message>(HttpStatus.OK);
		} else {

			return new ResponseEntity<Message>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ---------------------Get Messages by User----------------------------------
	@GetMapping("/getMessagesByUser/{senderUsername}/{receiverUserName}/{pageNumber}")
	public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable("senderUsername") String senderUserName,
			@PathVariable("receiverUserName") String receiverUserName, @PathVariable("pageNumber") int pageNumber,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<List<Message>>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<Message>>(
				messageDAO.getMessagesFromUser(senderUserName, receiverUserName, pageNumber), HttpStatus.OK);

	}

	// ---------------------Get Messages by Circle--------------------------------
	@GetMapping("/getMessagesByCircle/{circleName}/{pageNumber}")
	public ResponseEntity<List<Message>> getMessagesByCircle(@PathVariable("circleName") String circleName,
			@PathVariable("pageNumber") int pageNumber,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<List<Message>>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<Message>>(messageDAO.getMessagesFromCircle(circleName, pageNumber),
				HttpStatus.OK);

	}

	// ---------------------List All Tags--------------------------------
	@GetMapping("/listAllTags")
	public ResponseEntity<List<String>> listAllTags(HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<List<String>>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<String>>(messageDAO.listTags(), HttpStatus.OK);

	}

	// ---------------------Get Messages by Tag--------------------------------
	@GetMapping("/showMessagesWithTag/{tag}/{pageNumber}")
	public ResponseEntity<List<Message>> showMessagesWithTag(@PathVariable("tag") String tag,
			@PathVariable("pageNumber") int pageNumber,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<List<Message>>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<Message>>(messageDAO.showMessagesWithTag(tag, pageNumber), HttpStatus.OK);

	}

	/*----------------------Subscribe user to stream with a specific tag------------------------------------------------------------*/

	@PutMapping("/subscribe/{username}/{tag}")
	public ResponseEntity<UserTag> subscribeUserToTag(@PathVariable("username") String username,
			@PathVariable("tag") String tag,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<UserTag>(HttpStatus.UNAUTHORIZED);
		}
		boolean status = messageDAO.subscribeUserToTag(username, tag);
		if (status == false) {

			return new ResponseEntity<UserTag>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {

			return new ResponseEntity<UserTag>(HttpStatus.OK);
		}

	}

	/*----------------------Unsubscribe user to stream with a specific tag------------------------------------------------------------*/

	@PutMapping("/unsubscribe/{username}/{tag}")
	public ResponseEntity<UserTag> unsubscribeUserToTag(@PathVariable("username") String username,
			@PathVariable("tag") String tag,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<UserTag>(HttpStatus.UNAUTHORIZED);
		}
		boolean status = messageDAO.unsubscribeUserToTag(username, tag);
		if (status == false) {

			return new ResponseEntity<UserTag>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {

			return new ResponseEntity<UserTag>(HttpStatus.OK);
		}

	}

	// -----------------------Retrieve tags subscribed by a specific
	// user--------------------------------
	@GetMapping("/tags/search/user/{username}")
	public ResponseEntity<List<String>> getMyTags(@PathVariable("username") String userId,HttpSession session) {
		String loggedInUserName = (String) session.getAttribute("loggedInUserName");
		if(loggedInUserName==null) {
			return new ResponseEntity<List<String>>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<String>>(messageDAO.listMyTags(userId), HttpStatus.OK);

	}

}
