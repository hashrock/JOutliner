/*
 * Copyright (c) 2001, Maynard Demmon
 * Copyright (c) 2001, Organic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 * 
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *
 *  - Neither the name of Organic nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.util.misc;

import com.organic.maynard.util.math.*;
import java.util.*;

public class RandomQuote {

	public static String[] quotes = {
		"I'm not addicted - I'm pre-rehab.",
		"If you get here early, you have more hours to cause trouble.",
		"You could do a lot of good with a 12 inch pepper grinder.",
		"The L in user is silent.",
		"You guys are the circus freaks - I'm the normal one.",
		"We're just going to suck money out of them until they fail, right?",
		"Shock, denial, anger, bargaining, accpetance.",
		"I think I'm embracing the pain now.",
		"There's several people here who's internal monalogue I want nothing to do with, and you're one of them.",
		"Everyone else's truths are relative - mine are absolute.",
		"CE Weekly: Half project check-in, half group therapy.",
		"Tell the client to go see the Wizard, cuz they need to get a brain.",
		"I'm not saving lives here, I'm building websites.",
		"Look, you walk to close to the chimp cage, you're gonna get shit thrown at you.",
		"Everything is completely fucked, so I'm leaving.",
		"This is broken, but I'm not telling anyone because I don't want anyone to know.",
		"I thought the quacking meant it liked it.",
		"Isn't it a bit early to start drinking? Who Stops?",
		"Moliere sure loves sherry.",
		"That's too easy - where's the pain?",
		"Did you see that? She smiled at me. No man, that was just gas.",
		"I tell you, man, a bottle of NyQuil, a couple of bong hits... it's just like heroin.",
		"He's devious and insubordinate - two qualities I value highly.",
		"He's a nice guy, he's got a watch that farts.",
		"I trust her about as far as I can throw cheesecake underwater.",
		"He just wants to go get drunk! That's not true - there's a lot of hookers there too.",
		"I didn't know that today was \"Come to Work Loaded Day\"",
		"Ummm... we're not doing it that way. Well, then how are you doing it? <pause> it's a surprise.",
		"Aren't you Mr. Macho, Mr. Suction Cup?",
		"Yeah, the apocalypse is going to be expensive.",
		"Is this CVS or RCS or what? It's F-R-O-N-T-I-E-R",
		"It's harmless - it's just a little rat pee.",
		"You know, you've just been Mr. Negativity since the first day I shot an arrow by your head.",
		"That's when you start using Jedi mind power and brute force.",
		"We've just hit a major milestone - of course, you can't see anything, but we feel much better.",
		"It's all about establishing ass coverage.",
		"Meet my out by the bike racks in 15 minutes.",
		"Maybe it would help if I got out and pushed.",
		"It's not loaded! It was loaded when I gave it to you - check your pants.",
		"We're on a three wheeled wagon goin down suck-ass hill.",
		"Call in a Nerf hit.",
		"Organic is a great place to work because of all the great people we work with - which can be hard to remember when they're going batshit on you.",
		"Assume I'm retarded.",
		"Site blueprint? That's right up there with unlimited wealth and power.",
		"Is it discussional or computeral?",
		"My ass in your face!",
		"My Flaming ASS OF FURY in your face! -Gina",
		"I Kiss You. -Mahir"
	};
	
	public static String get() {
		int num = RandomNumber.get(0,quotes.length);
		return quotes[num];
	}
}